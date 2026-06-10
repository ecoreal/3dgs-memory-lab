package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.AiImageRequest;
import com.cqq._3dobject.dto.HunyuanSubmitRequest;
import com.cqq._3dobject.service.AiGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AiGenerationController {

    private final AiGatewayService aiGatewayService;

    public AiGenerationController(AiGatewayService aiGatewayService) {
        this.aiGatewayService = aiGatewayService;
    }

    @PostMapping("/hunyuan3d/submit")
    public Map<String, Object> submit(@RequestBody HunyuanSubmitRequest request) {
        return aiGatewayService.submitHunyuan(request);
    }

    @GetMapping("/hunyuan3d/query/{jobId}")
    public Object query(@PathVariable("jobId") String jobId) {
        return aiGatewayService.queryHunyuan(jobId, false);
    }

    @GetMapping("/hunyuan3d/query_fast/{jobId}")
    public Object queryFast(@PathVariable("jobId") String jobId) {
        return aiGatewayService.queryHunyuan(jobId, true);
    }

    @PostMapping("/seed3d/submit")
    public Map<String, Object> submitSeed3d(@RequestBody AiImageRequest request) {
        return aiGatewayService.submitSeed3d(request);
    }

    @GetMapping("/seed3d/query/{jobId}")
    public Map<String, Object> querySeed3d(@PathVariable("jobId") String jobId) {
        return aiGatewayService.querySeed3d(jobId);
    }

    @PostMapping("/seedream/enhance")
    public Map<String, Object> enhance(@RequestBody AiImageRequest request) {
        return aiGatewayService.enhance(request);
    }

    @PostMapping("/seedream/colorize")
    public Map<String, Object> colorize(@RequestBody AiImageRequest request) {
        return aiGatewayService.colorize(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(AiGatewayService.failResponse("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConfigError(IllegalStateException ex) {
        return ResponseEntity.internalServerError().body(AiGatewayService.failResponse("CONFIG_ERROR", ex.getMessage()));
    }
}
