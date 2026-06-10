package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.SceneSchemeRequest;
import com.cqq._3dobject.service.SceneSchemeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scene-schemes")
public class SceneSchemeController {

    private final SceneSchemeService sceneSchemeService;

    public SceneSchemeController(SceneSchemeService sceneSchemeService) {
        this.sceneSchemeService = sceneSchemeService;
    }

    @GetMapping("/mine")
    public List<Map<String, Object>> listMine(HttpServletRequest request) {
        Long currentUid = (Long) request.getAttribute("current_uid");
        return sceneSchemeService.listMine(currentUid);
    }

    @GetMapping("/public")
    public List<Map<String, Object>> listPublic(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "roomId", required = false) String roomId) {
        return sceneSchemeService.listPublic(keyword, roomId);
    }

    @GetMapping("/public/{schemeId}")
    public ResponseEntity<?> publicDetail(@PathVariable("schemeId") Long schemeId) {
        try {
            return ResponseEntity.ok(sceneSchemeService.getPublicDetail(schemeId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{schemeId}")
    public ResponseEntity<?> detail(@PathVariable("schemeId") Long schemeId, HttpServletRequest request) {
        try {
            Long currentUid = (Long) request.getAttribute("current_uid");
            return ResponseEntity.ok(sceneSchemeService.getDetail(currentUid, schemeId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SceneSchemeRequest request, HttpServletRequest servletRequest) {
        try {
            Long currentUid = (Long) servletRequest.getAttribute("current_uid");
            return ResponseEntity.ok(sceneSchemeService.create(currentUid, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{schemeId}")
    public ResponseEntity<?> update(@PathVariable("schemeId") Long schemeId,
                                    @RequestBody SceneSchemeRequest request,
                                    HttpServletRequest servletRequest) {
        try {
            Long currentUid = (Long) servletRequest.getAttribute("current_uid");
            return ResponseEntity.ok(sceneSchemeService.update(currentUid, schemeId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            int status = "方案不存在或无权限访问".equals(e.getMessage()) ? 404 : 500;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{schemeId}")
    public ResponseEntity<?> delete(@PathVariable("schemeId") Long schemeId, HttpServletRequest request) {
        try {
            Long currentUid = (Long) request.getAttribute("current_uid");
            sceneSchemeService.delete(currentUid, schemeId);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            int status = "方案不存在或无权限访问".equals(e.getMessage()) ? 404 : 500;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }
}
