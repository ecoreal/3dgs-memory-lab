package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.ModelFileUrlDTO;
import com.cqq._3dobject.entity.ModelFile;
import com.cqq._3dobject.service.ModelFileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/model-files")
public class ModelFileController {

    @Autowired
    private ModelFileService modelFileService;

    /**
     * 上传文件（支持单个或批量，多文件并行落盘）
     * POST /api/model-files/upload?mid=10&fileType=GLB
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("mid") Long mid,
            @RequestParam(value = "fileType", required = false) String fileType,
            HttpServletRequest request) {
        try {
            List<ModelFile> results = modelFileService.uploadFiles(mid, files, fileType);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 绑定已经生成好的模型文件 URL
     * POST /api/model-files/bind-url
     */
    @PostMapping("/bind-url")
    public ResponseEntity<?> bindFileUrl(@RequestBody ModelFileUrlDTO request, HttpServletRequest servletRequest) {
        try {
            ModelFile result = modelFileService.bindFileUrl(
                    request.getMid(),
                    request.getFileUrl(),
                    request.getFileType(),
                    request.getOriginalName(),
                    request.getFileSize(),
                    request.getContentType()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取模型的所有文件（可按类型过滤）
     * GET /api/model-files/model/{mid}?fileType=GLB
     */
    @GetMapping("/model/{mid}")
    public List<ModelFile> getFiles(@PathVariable("mid") Long mid,
                                    @RequestParam(value = "fileType", required = false) String fileType) {
        if (fileType != null && !fileType.isBlank()) {
            return modelFileService.getFilesByModelAndType(mid, fileType);
        }
        return modelFileService.getFilesByModel(mid);
    }

    /**
     * 获取单个文件信息 + 访问 URL
     * GET /api/model-files/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFileInfo(@PathVariable("id") Long id) {
        ModelFile file = modelFileService.getFileById(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("file", file);
        result.put("url", modelFileService.buildAccessUrl(file));
        return ResponseEntity.ok(result);
    }

    /**
     * 下载文件（流式输出）
     * GET /api/model-files/download/{id}
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") Long id) {
        try {
            ModelFile record = modelFileService.getFileById(id);
            if (record == null) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = modelFileService.getFileAsResource(id);

            String fileName = record.getOriginalName() != null
                    ? record.getOriginalName()
                    : "file-" + id;
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

            String contentType = record.getContentType() != null
                    ? record.getContentType()
                    : "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除单个文件（DB 同步删，磁盘异步删）
     * DELETE /api/model-files/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            modelFileService.deleteFile(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除模型全部文件（DB 同步删，磁盘异步删）
     * DELETE /api/model-files/model/{mid}
     */
    @DeleteMapping("/model/{mid}")
    public ResponseEntity<?> deleteModelFiles(@PathVariable("mid") Long mid, HttpServletRequest request) {
        try {
            modelFileService.deleteFilesByModel(mid);
            return ResponseEntity.ok(Map.of("message", "模型文件已全部删除"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
