package com.cqq._3dobject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.cqq._3dobject.dto.AiImageRequest;
import com.cqq._3dobject.service.AiGatewayService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private static final Logger log = LoggerFactory.getLogger(MediaController.class);

    private final String baseDir = System.getProperty("user.dir") + "/uploads";

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private AiGatewayService aiGatewayService;

    /**
     * 上传图片存储
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        return saveFile(file, "images");
    }

    /**
     * 上传音频存储
     */
    @PostMapping("/audio")
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        return saveFile(file, "audios");
    }

    /**
     * 上传图片调用远端 enhance 接口，返回处理后的图片
     */
    @PostMapping("/enhance")
    public ResponseEntity<?> enhanceImage(@RequestParam("file") MultipartFile file) {
        return callAiImageApi(file, true);
    }

    /**
     * 上传图片调用远端 colorize 接口，返回处理后的图片
     */
    @PostMapping("/colorize")
    public ResponseEntity<?> colorizeImage(@RequestParam("file") MultipartFile file) {
        return callAiImageApi(file, false);
    }

    /**
     * 后端下载远程图片后调用上色接口，避免浏览器读取第三方图片时遇到 CORS 限制
     */
    @PostMapping("/colorize-by-url")
    public ResponseEntity<?> colorizeImageByUrl(@RequestBody Map<String, String> request) {
        try {
            String imageUrl = request == null ? null : request.get("imageUrl");
            Map<String, Object> result = aiGatewayService.colorizeByUrl(imageUrl);
            return compatibleImageUrlResponse(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(502).body(Map.of("error", "远端接口调用失败: " + ex.getMessage()));
        }
    }

    private ResponseEntity<Map<String, String>> saveFile(MultipartFile file, String subDir) {
        try {
            Path targetDir = Paths.get(baseDir, subDir).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = (originalFileName != null && originalFileName.contains("."))
                    ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            String fileAccessUrl = baseUrl + "/uploads/" + subDir + "/" + fileName;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileAccessUrl);
            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            return ResponseEntity.status(500).body(Map.of("error", "上传失败: " + ex.getMessage()));
        }
    }

    private ResponseEntity<?> callAiImageApi(MultipartFile file, boolean enhance) {
        try {
            AiImageRequest request = new AiImageRequest();
            request.setImage_base64(aiGatewayService.toDataUri(file));
            request.setSize("2K");

            Map<String, Object> result = enhance
                    ? aiGatewayService.enhance(request)
                    : aiGatewayService.colorize(request);

            return compatibleImageUrlResponse(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(502).body(Map.of("error", "远端接口调用失败: " + ex.getMessage()));
        }
    }

    private ResponseEntity<?> compatibleImageUrlResponse(Map<String, Object> result) {
        if (result == null) {
            return ResponseEntity.status(502).body(Map.of("error", "远端接口调用失败: AI 服务响应为空", "success", false));
        }

        Object imageUrl = result.get("image_url");
        if (!(imageUrl instanceof String imageUrlStr) || imageUrlStr.isBlank()) {
            log.warn("AI image API returned no image_url, result={}", result);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            String remoteError = firstText(result, "ErrorMessage", "error_message", "message", "error");
            response.put("error", StringUtils.hasText(remoteError)
                    ? "远端接口调用失败: " + remoteError
                    : "远端接口调用失败");
            putIfPresent(response, "status", result.get("Status"));
            putIfPresent(response, "errorCode", firstText(result, "ErrorCode", "error_code", "code"));
            putIfPresent(response, "remoteError", remoteError);
            return ResponseEntity.status(502).body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("url", imageUrlStr);
        response.put("image_url", imageUrlStr);
        return ResponseEntity.ok(response);
    }

    private String firstText(Map<String, Object> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private void putIfPresent(Map<String, Object> target, String key, Object value) {
        if (value != null && StringUtils.hasText(String.valueOf(value))) {
            target.put(key, value);
        }
    }
}
