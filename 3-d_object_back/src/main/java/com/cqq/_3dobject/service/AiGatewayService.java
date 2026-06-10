package com.cqq._3dobject.service;

import com.cqq._3dobject.config.AiApiProperties;
import com.cqq._3dobject.dto.AiImageRequest;
import com.cqq._3dobject.dto.HunyuanSubmitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiGatewayService {

    private static final Logger log = LoggerFactory.getLogger(AiGatewayService.class);

    private static final Pattern IMAGE_DATA_URI_PATTERN = Pattern.compile(
            "^data:image/([a-zA-Z0-9.+-]+);base64,(.+)$",
            Pattern.DOTALL
    );
    private static final long MAX_HUNYUAN_IMAGE_BYTES = 6L * 1024 * 1024;
    private static final long MAX_SEEDREAM_IMAGE_BYTES = 10L * 1024 * 1024;
    private static final long MAX_SEED3D_IMAGE_BYTES = 10L * 1024 * 1024;
    private static final long MAX_SEEDREAM_PIXELS = 36_000_000L;
    private static final long MAX_SEED3D_PIXELS = 4096L * 4096L;

    private static final Set<String> HUNYUAN_IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> SEEDREAM_IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "webp", "bmp", "tiff", "gif");
    private static final Set<String> SEED3D_IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "webp", "bmp");
    private static final Set<String> ALLOWED_GENERATE_TYPES = Set.of("Fast", "Normal", "LowPoly", "Geometry", "Sketch");
    private static final Set<String> ALLOWED_SIZES = Set.of("2K", "4K");

    private final RestTemplate restTemplate;
    private final AiApiProperties aiApiProperties;

    public AiGatewayService(RestTemplate restTemplate, AiApiProperties aiApiProperties) {
        this.restTemplate = restTemplate;
        this.aiApiProperties = aiApiProperties;
    }

    public Map<String, Object> submitHunyuan(HunyuanSubmitRequest request) {
        HunyuanSubmitRequest normalized = normalizeSubmitRequest(request);
        String submitUrl = requiredUrl(aiApiProperties.getHunyuanSubmitUrl(), "app.ai.server-ip/app.ai.local-ip");
        Map<String, Object> response = postJson(
                submitUrl,
                normalized,
                failResponse("REQUEST_ERROR", "混元3D任务提交失败")
        );
        return normalizeSubmitResponse(response);
    }

    public Map<String, Object> queryHunyuan(String jobId, boolean fastMode) {
        String normalizedJobId = requireJobId(jobId);
        String baseUrl = fastMode
                ? requiredUrl(aiApiProperties.getHunyuanFastQueryUrl(), "app.ai.server-ip/app.ai.local-ip")
                : requiredUrl(aiApiProperties.getHunyuanQueryUrl(), "app.ai.server-ip/app.ai.local-ip");
        Map<String, Object> response = getJson(
                buildQueryUrl(baseUrl, normalizedJobId),
                failResponse(normalizedJobId, "QUERY_ERROR", "查询任务失败")
        );
        return normalizeTaskResponse(response, normalizedJobId);
    }

    public Map<String, Object> submitSeed3d(AiImageRequest request) {
        AiImageRequest normalized = normalizeSeed3dRequest(request);
        String submitUrl = requiredUrl(aiApiProperties.getSeed3dSubmitUrl(), "app.ai.server-ip/app.ai.local-ip");
        Map<String, Object> response = postJson(
                submitUrl,
                normalized,
                failResponse("REQUEST_ERROR", "豆包3D任务提交失败")
        );
        return normalizeSubmitResponse(response);
    }

    public Map<String, Object> querySeed3d(String jobId) {
        String normalizedJobId = requireJobId(jobId);
        String baseUrl = requiredUrl(aiApiProperties.getSeed3dQueryUrl(), "app.ai.server-ip/app.ai.local-ip");
        Map<String, Object> response = getJson(
                buildQueryUrl(baseUrl, normalizedJobId),
                failResponse(normalizedJobId, "QUERY_ERROR", "查询任务失败")
        );
        return normalizeTaskResponse(response, normalizedJobId);
    }

    public Map<String, Object> enhance(AiImageRequest request) {
        return callSeedream(
                request,
                requiredUrl(aiApiProperties.getSeedreamEnhanceUrl(), "app.ai.server-ip/app.ai.local-ip")
        );
    }

    public Map<String, Object> colorize(AiImageRequest request) {
        return callSeedream(
                request,
                requiredUrl(aiApiProperties.getSeedreamColorizeUrl(), "app.ai.server-ip/app.ai.local-ip")
        );
    }

    public Map<String, Object> colorizeByUrl(String imageUrl) {
        AiImageRequest request = new AiImageRequest();
        request.setImage_base64(downloadImageAsDataUri(imageUrl));
        request.setSize("2K");
        return colorize(request);
    }

    public String toDataUri(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String extension = extractExtension(file.getOriginalFilename());
        if (!SEEDREAM_IMAGE_TYPES.contains(extension)) {
            throw new IllegalArgumentException("仅支持 jpg、jpeg、png、webp、bmp、tiff、gif 图片");
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:image/" + normalizeMimeSubtype(extension) + ";base64," + base64;
        } catch (IOException ex) {
            throw new IllegalArgumentException("图片读取失败: " + ex.getMessage());
        }
    }

    private String downloadImageAsDataUri(String imageUrl) {
        URI uri = parseHttpUri(imageUrl);
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);
            byte[] body = response.getBody();
            if (body == null || body.length == 0) {
                throw new IllegalArgumentException("远程图片内容为空");
            }

            String imageType = resolveRemoteImageType(response.getHeaders().getContentType(), uri);
            String base64 = Base64.getEncoder().encodeToString(body);
            return "data:image/" + normalizeMimeSubtype(imageType) + ";base64," + base64;
        } catch (RestClientException ex) {
            throw new IllegalArgumentException("远程图片下载失败: " + ex.getMessage());
        }
    }

    private URI parseHttpUri(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new IllegalArgumentException("imageUrl 不能为空");
        }
        URI uri;
        try {
            uri = URI.create(imageUrl.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("imageUrl 格式错误");
        }
        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("imageUrl 仅支持 http 或 https");
        }
        return uri;
    }

    private String resolveRemoteImageType(MediaType contentType, URI uri) {
        if (contentType != null && "image".equalsIgnoreCase(contentType.getType())) {
            String subtype = normalizeImageType(contentType.getSubtype());
            if (SEEDREAM_IMAGE_TYPES.contains(subtype)) {
                return subtype;
            }
        }

        String extension = extractExtension(uri.getPath());
        if (SEEDREAM_IMAGE_TYPES.contains(extension)) {
            return extension;
        }
        throw new IllegalArgumentException("远程图片格式无法识别");
    }

    public static Map<String, Object> failResponse(String errorCode, String errorMessage) {
        return unifiedResponse("FAIL", null, null, errorCode, errorMessage, null);
    }

    private Map<String, Object> callSeedream(AiImageRequest request, String url) {
        AiImageRequest normalized = normalizeSeedreamRequest(request);
        Map<String, Object> response = postJson(
                url,
                normalized,
                failResponse("REQUEST_ERROR", "豆包图片生成失败")
        );
        return normalizeImageResponse(response);
    }

    private Map<String, Object> postJson(String url, Object request, Map<String, Object> fallback) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(request, headers);
            Map<?, ?> response = restTemplate.postForObject(url, entity, Map.class);
            return normalizeMap(response, fallback);
        } catch (RestClientException ex) {
            log.warn("AI POST request failed, url={}, error={}", url, ex.getMessage());
            return failResponse("REQUEST_ERROR", ex.getMessage());
        }
    }

    private Map<String, Object> getJson(String url, Map<String, Object> fallback) {
        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return normalizeMap(response, fallback);
        } catch (RestClientException ex) {
            Object jobId = fallback.get("job_id");
            return unifiedResponse("FAIL", jobId, null, "QUERY_ERROR", ex.getMessage(), null);
        }
    }

    private Map<String, Object> normalizeMap(Map<?, ?> response, Map<String, Object> fallback) {
        if (response == null) {
            return new LinkedHashMap<>(fallback);
        }
        Map<String, Object> normalized = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : response.entrySet()) {
            normalized.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return normalized;
    }

    private HunyuanSubmitRequest normalizeSubmitRequest(HunyuanSubmitRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        validateHunyuanImage(request.getImage_base64());
        String generateType = StringUtils.hasText(request.getGenerate_type()) ? request.getGenerate_type().trim() : "Normal";
        if (!ALLOWED_GENERATE_TYPES.contains(generateType)) {
            throw new IllegalArgumentException("generate_type 仅支持 Fast、Normal、LowPoly、Geometry、Sketch");
        }

        HunyuanSubmitRequest normalized = new HunyuanSubmitRequest();
        normalized.setImage_base64(request.getImage_base64().trim());
        normalized.setGenerate_type(generateType);
        return normalized;
    }

    private AiImageRequest normalizeSeedreamRequest(AiImageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        validateSeedreamImage(request.getImage_base64());
        String size = StringUtils.hasText(request.getSize()) ? request.getSize().trim().toUpperCase(Locale.ROOT) : "2K";
        if (!ALLOWED_SIZES.contains(size)) {
            throw new IllegalArgumentException("size 仅支持 2K 或 4K");
        }

        AiImageRequest normalized = new AiImageRequest();
        normalized.setImage_base64(request.getImage_base64().trim());
        normalized.setSize(size);
        return normalized;
    }

    private AiImageRequest normalizeSeed3dRequest(AiImageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        validateSeed3dImage(request.getImage_base64());

        AiImageRequest normalized = new AiImageRequest();
        normalized.setImage_base64(request.getImage_base64().trim());
        return normalized;
    }

    private void validateHunyuanImage(String imageBase64) {
        ImageData imageData = parseImageData(imageBase64, HUNYUAN_IMAGE_TYPES);
        if (imageData.bytes().length > MAX_HUNYUAN_IMAGE_BYTES) {
            throw new IllegalArgumentException("图片大小不能超过6MB");
        }
        if (imageData.width() < 128 || imageData.height() < 128) {
            throw new IllegalArgumentException("图片单边分辨率不能小于128像素");
        }
        if (imageData.width() > 5000 || imageData.height() > 5000) {
            throw new IllegalArgumentException("图片单边分辨率不能大于5000像素");
        }
    }

    private void validateSeedreamImage(String imageBase64) {
        ImageData imageData = parseImageData(imageBase64, SEEDREAM_IMAGE_TYPES);
        if (imageData.bytes().length >= MAX_SEEDREAM_IMAGE_BYTES) {
            throw new IllegalArgumentException("图片大小必须小于10MB");
        }
        if (imageData.width() <= 14 || imageData.height() <= 14) {
            throw new IllegalArgumentException("图片单边长度必须大于14像素");
        }
        long totalPixels = (long) imageData.width() * imageData.height();
        if (totalPixels >= MAX_SEEDREAM_PIXELS) {
            throw new IllegalArgumentException("图片总像素必须小于36000000");
        }
        double aspectRatio = (double) imageData.width() / imageData.height();
        if (aspectRatio < 1.0 / 16.0 || aspectRatio > 16.0) {
            throw new IllegalArgumentException("图片宽高比必须在[1/16,16]之间");
        }
    }

    private void validateSeed3dImage(String imageBase64) {
        ImageData imageData = parseImageData(imageBase64, SEED3D_IMAGE_TYPES);
        if (imageData.bytes().length > MAX_SEED3D_IMAGE_BYTES) {
            throw new IllegalArgumentException("图片大小不能超过10MB");
        }
        long totalPixels = (long) imageData.width() * imageData.height();
        if (totalPixels >= MAX_SEED3D_PIXELS) {
            throw new IllegalArgumentException("图片总像素必须小于4096x4096");
        }
    }

    private ImageData parseImageData(String imageBase64, Set<String> allowedTypes) {
        if (!StringUtils.hasText(imageBase64)) {
            throw new IllegalArgumentException("image_base64 不能为空");
        }

        Matcher matcher = IMAGE_DATA_URI_PATTERN.matcher(imageBase64.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("image_base64 格式错误，应为 data:image/<格式>;base64,<编码>");
        }

        String imageType = normalizeImageType(matcher.group(1));
        if (!allowedTypes.contains(imageType)) {
            throw new IllegalArgumentException("不支持的图片格式: " + imageType);
        }

        byte[] decoded;
        try {
            String encodedPart = matcher.group(2).replaceAll("\\s+", "");
            decoded = Base64.getDecoder().decode(encodedPart);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("image_base64 不是有效的 Base64 图片");
        }
        if (decoded.length == 0) {
            throw new IllegalArgumentException("image_base64 不能为空图片");
        }

        int[] dimensions = readImageDimensions(imageType, decoded);
        return new ImageData(imageType, decoded, dimensions[0], dimensions[1]);
    }

    private int[] readImageDimensions(String imageType, byte[] bytes) {
        return switch (imageType) {
            case "jpg", "jpeg" -> readJpegDimensions(bytes);
            case "png" -> readPngDimensions(bytes);
            case "gif" -> readGifDimensions(bytes);
            case "bmp" -> readBmpDimensions(bytes);
            case "webp" -> readWebpDimensions(bytes);
            case "tiff" -> readTiffDimensions(bytes);
            default -> throw new IllegalArgumentException("无法读取图片尺寸");
        };
    }

    private int[] readPngDimensions(byte[] bytes) {
        if (bytes.length < 24 || bytes[0] != (byte) 0x89 || bytes[1] != 0x50 || bytes[2] != 0x4E || bytes[3] != 0x47) {
            throw new IllegalArgumentException("PNG图片头无效");
        }
        return new int[]{readInt32(bytes, 16, ByteOrder.BIG_ENDIAN), readInt32(bytes, 20, ByteOrder.BIG_ENDIAN)};
    }

    private int[] readGifDimensions(byte[] bytes) {
        if (bytes.length < 10 || !(startsWith(bytes, "GIF87a") || startsWith(bytes, "GIF89a"))) {
            throw new IllegalArgumentException("GIF图片头无效");
        }
        return new int[]{readUInt16(bytes, 6, ByteOrder.LITTLE_ENDIAN), readUInt16(bytes, 8, ByteOrder.LITTLE_ENDIAN)};
    }

    private int[] readBmpDimensions(byte[] bytes) {
        if (bytes.length < 26 || bytes[0] != 0x42 || bytes[1] != 0x4D) {
            throw new IllegalArgumentException("BMP图片头无效");
        }
        int width = readInt32(bytes, 18, ByteOrder.LITTLE_ENDIAN);
        int height = Math.abs(readInt32(bytes, 22, ByteOrder.LITTLE_ENDIAN));
        return new int[]{width, height};
    }

    private int[] readJpegDimensions(byte[] bytes) {
        if (bytes.length < 4 || bytes[0] != (byte) 0xFF || bytes[1] != (byte) 0xD8) {
            throw new IllegalArgumentException("JPEG图片头无效");
        }

        int index = 2;
        while (index < bytes.length) {
            while (index < bytes.length && (bytes[index] & 0xFF) != 0xFF) {
                index++;
            }
            while (index < bytes.length && (bytes[index] & 0xFF) == 0xFF) {
                index++;
            }
            if (index >= bytes.length) {
                break;
            }

            int marker = bytes[index++] & 0xFF;
            if (marker == 0x01 || (marker >= 0xD0 && marker <= 0xD8)) {
                continue;
            }
            if (index + 2 > bytes.length) {
                break;
            }

            int segmentLength = readUInt16(bytes, index, ByteOrder.BIG_ENDIAN);
            if (segmentLength < 2 || index + segmentLength > bytes.length) {
                break;
            }
            if (isJpegSofMarker(marker)) {
                int height = readUInt16(bytes, index + 3, ByteOrder.BIG_ENDIAN);
                int width = readUInt16(bytes, index + 5, ByteOrder.BIG_ENDIAN);
                return new int[]{width, height};
            }
            index += segmentLength;
        }
        throw new IllegalArgumentException("无法读取JPEG图片尺寸");
    }

    private int[] readWebpDimensions(byte[] bytes) {
        if (bytes.length < 30 || !startsWith(bytes, "RIFF") || !asciiEquals(bytes, 8, "WEBP")) {
            throw new IllegalArgumentException("WebP图片头无效");
        }

        if (asciiEquals(bytes, 12, "VP8X")) {
            int width = readUInt24(bytes, 24) + 1;
            int height = readUInt24(bytes, 27) + 1;
            return new int[]{width, height};
        }
        if (asciiEquals(bytes, 12, "VP8L")) {
            int bits = readInt32(bytes, 21, ByteOrder.LITTLE_ENDIAN);
            int width = (bits & 0x3FFF) + 1;
            int height = ((bits >> 14) & 0x3FFF) + 1;
            return new int[]{width, height};
        }
        if (asciiEquals(bytes, 12, "VP8 ") && bytes[23] == (byte) 0x9D && bytes[24] == 0x01 && bytes[25] == 0x2A) {
            int width = readUInt16(bytes, 26, ByteOrder.LITTLE_ENDIAN) & 0x3FFF;
            int height = readUInt16(bytes, 28, ByteOrder.LITTLE_ENDIAN) & 0x3FFF;
            return new int[]{width, height};
        }
        throw new IllegalArgumentException("无法读取WebP图片尺寸");
    }

    private int[] readTiffDimensions(byte[] bytes) {
        if (bytes.length < 8) {
            throw new IllegalArgumentException("TIFF图片头无效");
        }
        ByteOrder order;
        if (bytes[0] == 0x49 && bytes[1] == 0x49) {
            order = ByteOrder.LITTLE_ENDIAN;
        } else if (bytes[0] == 0x4D && bytes[1] == 0x4D) {
            order = ByteOrder.BIG_ENDIAN;
        } else {
            throw new IllegalArgumentException("TIFF图片头无效");
        }
        if (readUInt16(bytes, 2, order) != 42) {
            throw new IllegalArgumentException("TIFF图片头无效");
        }

        int offset = readInt32(bytes, 4, order);
        if (offset < 0 || offset + 2 > bytes.length) {
            throw new IllegalArgumentException("无法读取TIFF图片尺寸");
        }
        int entryCount = readUInt16(bytes, offset, order);
        int cursor = offset + 2;
        Integer width = null;
        Integer height = null;
        for (int i = 0; i < entryCount && cursor + 12 <= bytes.length; i++) {
            int tag = readUInt16(bytes, cursor, order);
            int fieldType = readUInt16(bytes, cursor + 2, order);
            if (tag == 256 || tag == 257) {
                Integer value = switch (fieldType) {
                    case 3 -> readUInt16(bytes, cursor + 8, order);
                    case 4 -> readInt32(bytes, cursor + 8, order);
                    default -> null;
                };
                if (tag == 256) {
                    width = value;
                } else {
                    height = value;
                }
            }
            cursor += 12;
        }
        if (width == null || height == null) {
            throw new IllegalArgumentException("无法读取TIFF图片尺寸");
        }
        return new int[]{width, height};
    }

    private Map<String, Object> normalizeSubmitResponse(Map<String, Object> response) {
        if (response.containsKey("Status")) {
            return ensureUnifiedResponse(response, null);
        }
        if (Boolean.TRUE.equals(response.get("success"))) {
            return unifiedResponse("WAIT", response.get("job_id"), null, null, null, null);
        }
        return unifiedResponse(
                "FAIL",
                response.get("job_id"),
                null,
                stringValue(response.getOrDefault("ErrorCode", "SUBMIT_ERROR")),
                stringValue(response.getOrDefault("ErrorMessage", "任务提交失败")),
                null
        );
    }

    private Map<String, Object> normalizeTaskResponse(Map<String, Object> response, String jobId) {
        if (response.containsKey("Status")) {
            return ensureUnifiedResponse(response, jobId);
        }
        return unifiedResponse(
                "FAIL",
                jobId,
                null,
                "QUERY_ERROR",
                "AI 服务未返回结构化任务结果",
                null
        );
    }

    private Map<String, Object> normalizeImageResponse(Map<String, Object> response) {
        if (response.containsKey("Status")) {
            return ensureUnifiedResponse(response, null);
        }
        if (Boolean.TRUE.equals(response.get("success")) && response.get("image_url") != null) {
            return unifiedResponse("DONE", null, null, null, null, response.get("image_url"));
        }
        return unifiedResponse(
                "FAIL",
                null,
                null,
                firstString(response, "ErrorCode", "error_code", "code", "REQUEST_ERROR"),
                firstString(response, "ErrorMessage", "error_message", "message", "error", "图片生成失败"),
                null
        );
    }

    private Map<String, Object> ensureUnifiedResponse(Map<String, Object> response, Object defaultJobId) {
        return unifiedResponse(
                stringValue(response.get("Status")),
                response.getOrDefault("job_id", defaultJobId),
                response.get("ResultFile3Ds"),
                response.get("ErrorCode"),
                response.get("ErrorMessage"),
                response.get("image_url")
        );
    }

    private static Map<String, Object> unifiedResponse(
            String status,
            Object jobId,
            Object resultFile3ds,
            Object errorCode,
            Object errorMessage,
            Object imageUrl
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("Status", status);
        result.put("job_id", jobId);
        result.put("ResultFile3Ds", resultFile3ds);
        result.put("ErrorCode", errorCode);
        result.put("ErrorMessage", errorMessage);
        result.put("image_url", imageUrl);
        return result;
    }

    private static Map<String, Object> failResponse(Object jobId, String errorCode, String errorMessage) {
        return unifiedResponse("FAIL", jobId, null, errorCode, errorMessage, null);
    }

    private String requiredUrl(String url, String propertyName) {
        if (!StringUtils.hasText(url)) {
            throw new IllegalStateException(propertyName + " 未配置");
        }
        return url.trim();
    }

    private String requireJobId(String jobId) {
        if (!StringUtils.hasText(jobId)) {
            throw new IllegalArgumentException("job_id 不能为空");
        }
        return jobId.trim();
    }

    private String buildQueryUrl(String baseUrl, String jobId) {
        String normalized = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalized + "/" + URLEncoder.encode(jobId, StandardCharsets.UTF_8);
    }

    private String extractExtension(String fileName) {
        if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
            return "";
        }
        return normalizeImageType(fileName.substring(fileName.lastIndexOf('.') + 1));
    }

    private String normalizeMimeSubtype(String extension) {
        return "jpg".equals(extension) ? "jpeg" : extension;
    }

    private String normalizeImageType(String imageType) {
        String normalized = imageType.toLowerCase(Locale.ROOT);
        if ("pjpeg".equals(normalized)) {
            return "jpeg";
        }
        if ("tif".equals(normalized) || "x-tiff".equals(normalized)) {
            return "tiff";
        }
        if ("x-png".equals(normalized)) {
            return "png";
        }
        return normalized;
    }

    private boolean startsWith(byte[] bytes, String value) {
        return asciiEquals(bytes, 0, value);
    }

    private boolean asciiEquals(byte[] bytes, int offset, String value) {
        byte[] expected = value.getBytes(StandardCharsets.US_ASCII);
        if (offset < 0 || offset + expected.length > bytes.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (bytes[offset + i] != expected[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isJpegSofMarker(int marker) {
        return List.of(0xC0, 0xC1, 0xC2, 0xC3, 0xC5, 0xC6, 0xC7, 0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF)
                .contains(marker);
    }

    private int readUInt16(byte[] bytes, int offset, ByteOrder order) {
        return Short.toUnsignedInt(ByteBuffer.wrap(bytes, offset, 2).order(order).getShort());
    }

    private int readInt32(byte[] bytes, int offset, ByteOrder order) {
        return ByteBuffer.wrap(bytes, offset, 4).order(order).getInt();
    }

    private int readUInt24(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8) | ((bytes[offset + 2] & 0xFF) << 16);
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String firstString(Map<String, Object> response, String... keysAndFallback) {
        for (int i = 0; i < keysAndFallback.length - 1; i++) {
            Object value = response.get(keysAndFallback[i]);
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return keysAndFallback[keysAndFallback.length - 1];
    }

    private record ImageData(String type, byte[] bytes, int width, int height) {
    }
}
