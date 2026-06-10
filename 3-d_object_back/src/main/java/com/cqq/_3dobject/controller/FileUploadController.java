package com.cqq._3dobject.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final String baseDir = System.getProperty("user.dir") + "/uploads";

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * 修改后的上传接口
     * @param file 文件对象
     * @param type 文件夹类型 (传入 "avatar"、"workshop" 或 "scene-cover")
     */
    @PostMapping("/{type}")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable("type") String type) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "文件不能为空"));
            }

            if (!("avatar".equals(type) || "workshop".equals(type) || "scene-cover".equals(type))) {
                return ResponseEntity.badRequest().body(Map.of("error", "不支持的上传类型"));
            }

            // 1. 动态构造存储路径 (例如: uploads/workshop)
            Path targetDir = Paths.get(baseDir, type).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 2. 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String fileExtension = (originalFileName != null && originalFileName.contains(".")) ?
                    originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 3. 保存文件
            Path targetLocation = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            // 4. 返回完整访问 URL
            String fileAccessUrl = baseUrl + "/uploads/" + type + "/" + fileName;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileAccessUrl);

            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            return ResponseEntity.status(500).body(Map.of("error", "上传失败: " + ex.getMessage()));
        }
    }
}
