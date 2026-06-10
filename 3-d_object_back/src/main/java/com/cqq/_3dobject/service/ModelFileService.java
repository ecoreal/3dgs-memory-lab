package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.ModelFile;
import com.cqq._3dobject.repository.ModelFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

@Service
public class ModelFileService {

    private static final List<String> PREVIEW_MODEL_EXTENSIONS = List.of(".glb", ".gltf", ".obj", ".ply");

    private final Path baseUploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "models");
    private final Path projectRoot = Paths.get(System.getProperty("user.dir"));

    @Autowired
    private ModelFileRepository modelFileRepository;

    @Autowired
    @Qualifier("fileExecutor")
    private Executor fileExecutor;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * 单文件上传：transferTo 落盘（零拷贝，比 Files.copy 快）+ 写 DB
     */
    public ModelFile uploadFile(Long mid, MultipartFile file, String fileType) throws IOException {
        Path modelDir = baseUploadDir.resolve(String.valueOf(mid)).toAbsolutePath().normalize();
        if (!Files.exists(modelDir)) {
            Files.createDirectories(modelDir);
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        Path targetPath = modelDir.resolve(storedName);

        // transferTo 直接把临时文件 move 到目标位置，避免流拷贝
        file.transferTo(targetPath.toFile());

        if (fileType == null || fileType.isBlank()) {
            fileType = inferFileType(ext);
        }

        ModelFile record = new ModelFile();
        record.setMid(mid);
        record.setFileType(fileType.toUpperCase());
        record.setFilePath("uploads/models/" + mid + "/" + storedName);
        record.setOriginalName(originalName);
        record.setFileSize(file.getSize());
        record.setContentType(file.getContentType());
        return modelFileRepository.save(record);
    }

    /**
     * 批量上传：并行落盘，显著提升多文件场景速度
     */
    public List<ModelFile> uploadFiles(Long mid, List<MultipartFile> files, String fileType) {
        // 先确保目录存在（只建一次）
        Path modelDir = baseUploadDir.resolve(String.valueOf(mid)).toAbsolutePath().normalize();
        try {
            if (!Files.exists(modelDir)) {
                Files.createDirectories(modelDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("创建目录失败", e);
        }

        // 并行上传
        List<CompletableFuture<ModelFile>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return uploadFile(mid, file, fileType);
                    } catch (IOException e) {
                        throw new RuntimeException("上传失败: " + file.getOriginalFilename(), e);
                    }
                }, fileExecutor))
                .toList();

        // 等待全部完成并收集结果
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public List<ModelFile> getFilesByModel(Long mid) {
        return modelFileRepository.findByMid(mid);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getDistinctFormatsByModelIds(Collection<Long> mids) {
        if (mids == null || mids.isEmpty()) {
            return Map.of();
        }

        return modelFileRepository.findByMidIn(mids).stream()
                .collect(Collectors.groupingBy(
                        ModelFile::getMid,
                        Collectors.collectingAndThen(
                                Collectors.mapping(ModelFile::getFileType, Collectors.toCollection(java.util.LinkedHashSet::new)),
                                List::copyOf
                        )
                ));
    }

    public List<ModelFile> getFilesByModelAndType(Long mid, String fileType) {
        return modelFileRepository.findByMidAndFileType(mid, fileType.toUpperCase());
    }

    @Transactional
    public ModelFile bindFileUrl(Long mid, String fileUrl, String fileType,
                                 String originalName, Long fileSize, String contentType) {
        if (mid == null) {
            throw new IllegalArgumentException("mid 不能为空");
        }
        String normalizedUrl = normalizeFileUrl(fileUrl);
        String resolvedOriginalName = StringUtils.hasText(originalName)
                ? originalName.trim()
                : extractOriginalName(normalizedUrl);
        String resolvedType = StringUtils.hasText(fileType)
                ? fileType.trim()
                : inferFileType(extensionOf(resolvedOriginalName, normalizedUrl));
        String resolvedContentType = StringUtils.hasText(contentType)
                ? contentType.trim()
                : inferContentType(resolvedType);
        Long resolvedFileSize = fileSize;

        if (isAbsoluteHttpUrl(normalizedUrl)) {
            try {
                MaterializedModelFile materialized = materializeRemoteModelFile(mid, normalizedUrl, resolvedOriginalName);
                normalizedUrl = materialized.relativePath();
                resolvedOriginalName = materialized.originalName();
                resolvedType = materialized.fileType();
                resolvedContentType = materialized.contentType();
                resolvedFileSize = materialized.fileSize();
            } catch (IOException e) {
                throw new RuntimeException("远程模型文件下载或解压失败: " + e.getMessage(), e);
            }
        }

        ModelFile record = new ModelFile();
        record.setMid(mid);
        record.setFileType(resolvedType.toUpperCase());
        record.setFilePath(normalizedUrl);
        record.setOriginalName(resolvedOriginalName);
        record.setFileSize(resolvedFileSize);
        record.setContentType(resolvedContentType);
        return modelFileRepository.save(record);
    }

    public ModelFile getFileById(Long id) {
        return modelFileRepository.findById(id).orElse(null);
    }

    public Resource getFileAsResource(Long id) throws IOException {
        ModelFile record = modelFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件记录不存在: " + id));

        Path filePath = projectRoot.resolve(record.getFilePath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("磁盘文件不存在: " + filePath);
        }
        return resource;
    }

    /**
     * 删除单个文件：先删 DB，再异步删磁盘
     */
    @Transactional
    public void deleteFile(Long id) {
        ModelFile record = modelFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件记录不存在: " + id));
        String path = record.getFilePath();
        modelFileRepository.deleteById(id);
        // DB 已删除，磁盘清理异步进行
        asyncDeleteFromDisk(path);
    }

    /**
     * 删除模型全部文件：先删 DB，再异步清理磁盘
     */
    @Transactional
    public void deleteFilesByModel(Long mid) {
        List<ModelFile> files = modelFileRepository.findByMid(mid);
        List<String> paths = files.stream().map(ModelFile::getFilePath).toList();
        modelFileRepository.deleteByMid(mid);
        // 异步批量清理磁盘
        asyncBatchDeleteFromDisk(paths);
    }

    /**
     * 异步删除磁盘文件，不阻塞主线程
     */
    @Async("fileExecutor")
    public void asyncDeleteFromDisk(String relativePath) {
        try {
            Path filePath = projectRoot.resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }

    /**
     * 异步批量删除
     */
    @Async("fileExecutor")
    public void asyncBatchDeleteFromDisk(List<String> relativePaths) {
        for (String p : relativePaths) {
            try {
                Path filePath = projectRoot.resolve(p).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {
            }
        }
    }

    public String buildAccessUrl(ModelFile file) {
        if (isAbsoluteHttpUrl(file.getFilePath())) {
            return file.getFilePath();
        }
        return baseUrl + "/" + file.getFilePath();
    }

    private String normalizeFileUrl(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("fileUrl 不能为空");
        }

        String trimmed = fileUrl.trim();
        if (trimmed.startsWith("/uploads/")) {
            return trimmed.substring(1);
        }
        if (trimmed.startsWith("uploads/") || trimmed.startsWith("/static/") || trimmed.startsWith("static/")) {
            return trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
        }
        if (isAbsoluteHttpUrl(trimmed)) {
            return trimmed;
        }
        throw new IllegalArgumentException("fileUrl 仅支持 http(s) URL、/uploads/... 或 /static/...");
    }

    private boolean isAbsoluteHttpUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && StringUtils.hasText(uri.getHost());
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private String extractOriginalName(String fileUrl) {
        String path = fileUrl;
        try {
            URI uri = new URI(fileUrl);
            if (StringUtils.hasText(uri.getPath())) {
                path = uri.getPath();
            }
        } catch (URISyntaxException ignored) {
        }

        int slash = path.lastIndexOf('/');
        String name = slash >= 0 ? path.substring(slash + 1) : path;
        if (!StringUtils.hasText(name)) {
            return "model-file";
        }
        int query = name.indexOf('?');
        if (query >= 0) {
            name = name.substring(0, query);
        }
        return StringUtils.hasText(name) ? name : "model-file";
    }

    private MaterializedModelFile materializeRemoteModelFile(Long mid, String fileUrl, String fallbackName) throws IOException {
        Path modelDir = baseUploadDir.resolve(String.valueOf(mid)).toAbsolutePath().normalize();
        Files.createDirectories(modelDir);

        DownloadedRemoteFile downloaded = downloadRemoteFile(fileUrl);
        try {
            if (isZipFile(downloaded.tempPath(), downloaded.contentType(), fallbackName)) {
                return extractPreviewModelFromZip(mid, modelDir, downloaded.tempPath());
            }

            String ext = extensionOf(fallbackName, fileUrl).toLowerCase();
            if (!PREVIEW_MODEL_EXTENSIONS.contains(ext)) {
                ext = inferPreviewExtension(downloaded.contentType());
            }
            if (!PREVIEW_MODEL_EXTENSIONS.contains(ext)) {
                throw new IllegalArgumentException("远程文件不是可预览模型，也不是包含模型的 zip 压缩包");
            }

            String storedName = UUID.randomUUID() + ext;
            Path targetPath = modelDir.resolve(storedName);
            Files.move(downloaded.tempPath(), targetPath);
            return new MaterializedModelFile(
                    "uploads/models/" + mid + "/" + storedName,
                    StringUtils.hasText(fallbackName) ? fallbackName : storedName,
                    inferFileType(ext),
                    inferContentType(inferFileType(ext)),
                    Files.size(targetPath)
            );
        } finally {
            Files.deleteIfExists(downloaded.tempPath());
        }
    }

    private DownloadedRemoteFile downloadRemoteFile(String fileUrl) throws IOException {
        URL url = URI.create(fileUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(15_000);
        connection.setReadTimeout(120_000);
        connection.setRequestProperty("User-Agent", "3d-object-backend/1.0");

        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IOException("远程文件下载失败，HTTP " + status);
        }

        String contentType = connection.getContentType();
        Path tempPath = Files.createTempFile("model-download-", ".tmp");
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, tempPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } finally {
            connection.disconnect();
        }
        return new DownloadedRemoteFile(tempPath, contentType);
    }

    private boolean isZipFile(Path filePath, String contentType, String fileName) throws IOException {
        if (StringUtils.hasText(contentType) && contentType.toLowerCase().contains("zip")) {
            return true;
        }
        if (StringUtils.hasText(fileName) && fileName.toLowerCase().endsWith(".zip")) {
            return true;
        }
        byte[] header = new byte[4];
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            int read = inputStream.read(header);
            return read == 4 && header[0] == 'P' && header[1] == 'K';
        }
    }

    private MaterializedModelFile extractPreviewModelFromZip(Long mid, Path modelDir, Path zipPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            List<ZipEntry> entries = zipFile.stream()
                    .filter(entry -> !entry.isDirectory())
                    .filter(entry -> PREVIEW_MODEL_EXTENSIONS.contains(extensionOf(entry.getName(), entry.getName()).toLowerCase()))
                    .collect(Collectors.toCollection(ArrayList::new));
            entries.sort(Comparator.comparingInt(entry ->
                    PREVIEW_MODEL_EXTENSIONS.indexOf(extensionOf(entry.getName(), entry.getName()).toLowerCase())
            ));

            if (entries.isEmpty()) {
                throw new IllegalArgumentException("压缩包中未找到 glb/gltf/obj/ply 模型文件");
            }

            ZipEntry entry = entries.get(0);
            String entryName = entry.getName();
            String ext = extensionOf(entryName, entryName).toLowerCase();
            String storedName = UUID.randomUUID() + ext;
            Path targetPath = modelDir.resolve(storedName);
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                Files.copy(inputStream, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            return new MaterializedModelFile(
                    "uploads/models/" + mid + "/" + storedName,
                    extractOriginalName(entryName),
                    inferFileType(ext),
                    inferContentType(inferFileType(ext)),
                    Files.size(targetPath)
            );
        }
    }

    private String inferPreviewExtension(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "";
        }
        String lower = contentType.toLowerCase();
        if (lower.contains("gltf-binary")) {
            return ".glb";
        }
        if (lower.contains("gltf+json") || lower.contains("model/gltf")) {
            return ".gltf";
        }
        return "";
    }

    private String extensionOf(String originalName, String fileUrl) {
        String source = StringUtils.hasText(originalName) ? originalName : extractOriginalName(fileUrl);
        int dot = source.lastIndexOf('.');
        return dot >= 0 ? source.substring(dot) : "";
    }

    private String inferContentType(String fileType) {
        if (!StringUtils.hasText(fileType)) {
            return "application/octet-stream";
        }
        return switch (fileType.trim().toUpperCase()) {
            case "GLB" -> "model/gltf-binary";
            case "GLTF" -> "model/gltf+json";
            case "OBJ" -> "model/obj";
            case "PLY" -> "application/octet-stream";
            default -> "application/octet-stream";
        };
    }

    private String inferFileType(String ext) {
        if (ext == null || ext.isEmpty()) return "OTHER";
        return switch (ext.toLowerCase()) {
            case ".glb", ".gltf" -> "GLB";
            case ".obj" -> "OBJ";
            case ".fbx" -> "FBX";
            case ".stl" -> "STL";
            case ".3ds" -> "3DS";
            case ".dae" -> "DAE";
            case ".ply" -> "PLY";
            case ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp" -> "IMAGE";
            case ".mp3", ".wav", ".ogg", ".m4a", ".aac" -> "AUDIO";
            case ".mp4", ".mov", ".avi", ".webm" -> "VIDEO";
            case ".pdf" -> "PDF";
            case ".zip", ".rar", ".7z", ".tar", ".gz" -> "ARCHIVE";
            default -> ext.substring(1).toUpperCase();
        };
    }

    private record DownloadedRemoteFile(Path tempPath, String contentType) {
    }

    private record MaterializedModelFile(String relativePath, String originalName, String fileType,
                                         String contentType, Long fileSize) {
    }
}
