package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.AiImageRequest;
import com.cqq._3dobject.service.AiGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MediaControllerTest {

    @Test
    void enhanceShouldReturnCompatibleUrlFields() throws Exception {
        AiGatewayService aiGatewayService = mock(AiGatewayService.class);
        when(aiGatewayService.toDataUri(any())).thenReturn("data:image/png;base64,aGVsbG8=");
        when(aiGatewayService.enhance(any(AiImageRequest.class)))
                .thenReturn(Map.of("success", true, "image_url", "https://img.example/enhanced.jpg"));

        MediaController controller = new MediaController();
        ReflectionTestUtils.setField(controller, "aiGatewayService", aiGatewayService);
        ReflectionTestUtils.setField(controller, "baseUrl", "http://localhost:8080");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.png",
                MediaType.IMAGE_PNG_VALUE,
                "hello".getBytes()
        );

        mockMvc.perform(multipart("/api/media/enhance").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://img.example/enhanced.jpg"))
                .andExpect(jsonPath("$.image_url").value("https://img.example/enhanced.jpg"));
    }

    @Test
    void enhanceShouldReturnRemoteErrorWhenImageUrlMissing() throws Exception {
        AiGatewayService aiGatewayService = mock(AiGatewayService.class);
        when(aiGatewayService.toDataUri(any())).thenReturn("data:image/png;base64,aGVsbG8=");
        when(aiGatewayService.enhance(any(AiImageRequest.class)))
                .thenReturn(Map.of(
                        "Status", "FAIL",
                        "ErrorCode", "REQUEST_ERROR",
                        "ErrorMessage", "豆包接口超时"
                ));

        MediaController controller = new MediaController();
        ReflectionTestUtils.setField(controller, "aiGatewayService", aiGatewayService);
        ReflectionTestUtils.setField(controller, "baseUrl", "http://localhost:8080");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.png",
                MediaType.IMAGE_PNG_VALUE,
                "hello".getBytes()
        );

        mockMvc.perform(multipart("/api/media/enhance").file(file))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("远端接口调用失败: 豆包接口超时"))
                .andExpect(jsonPath("$.status").value("FAIL"))
                .andExpect(jsonPath("$.errorCode").value("REQUEST_ERROR"))
                .andExpect(jsonPath("$.remoteError").value("豆包接口超时"));
    }

    @Test
    void colorizeByUrlShouldReturnCompatibleUrlFields() throws Exception {
        AiGatewayService aiGatewayService = mock(AiGatewayService.class);
        when(aiGatewayService.colorizeByUrl(eq("https://img.example/enhanced.png")))
                .thenReturn(Map.of("Status", "DONE", "image_url", "https://img.example/colorized.jpg"));

        MediaController controller = new MediaController();
        ReflectionTestUtils.setField(controller, "aiGatewayService", aiGatewayService);
        ReflectionTestUtils.setField(controller, "baseUrl", "http://localhost:8080");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/media/colorize-by-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"imageUrl":"https://img.example/enhanced.png"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://img.example/colorized.jpg"))
                .andExpect(jsonPath("$.image_url").value("https://img.example/colorized.jpg"));
    }
}
