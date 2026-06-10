package com.cqq._3dobject.service;

import com.cqq._3dobject.config.AiApiProperties;
import com.cqq._3dobject.dto.AiImageRequest;
import com.cqq._3dobject.dto.HunyuanSubmitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AiGatewayServiceTest {

    private static final String PNG_128 = pngDataUri(128, 128);
    private static final String PNG_16 = pngDataUri(16, 16);

    private AiGatewayService aiGatewayService;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        AiApiProperties properties = new AiApiProperties();
        properties.setLocalIp("127.0.0.1");
        properties.setPort(8000);

        aiGatewayService = new AiGatewayService(restTemplate, properties);
    }

    @Test
    void submitHunyuanShouldDefaultGenerateTypeAndReturnUnifiedJobId() {
        server.expect(requestTo("http://127.0.0.1:8000/hunyuan3d/submit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "image_base64": "%s",
                          "generate_type": "Normal"
                        }
                        """.formatted(PNG_128), true))
                .andRespond(withSuccess("""
                        {
                          "Status":"WAIT",
                          "job_id":"job-123",
                          "ResultFile3Ds":null,
                          "ErrorCode":null,
                          "ErrorMessage":null,
                          "image_url":null
                        }
                        """, MediaType.APPLICATION_JSON));

        HunyuanSubmitRequest request = new HunyuanSubmitRequest();
        request.setImage_base64(PNG_128);

        Map<String, Object> response = aiGatewayService.submitHunyuan(request);

        assertEquals("WAIT", response.get("Status"));
        assertEquals("job-123", response.get("job_id"));
        server.verify();
    }

    @Test
    void submitHunyuanShouldRejectImageBelowMinimumSide() {
        HunyuanSubmitRequest request = new HunyuanSubmitRequest();
        request.setImage_base64(PNG_16);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> aiGatewayService.submitHunyuan(request));

        assertEquals("图片单边分辨率不能小于128像素", ex.getMessage());
    }

    @Test
    void queryHunyuanShouldReturnUnifiedFailureOnRemoteFailure() {
        server.expect(requestTo("http://127.0.0.1:8000/hunyuan3d/query/job-404"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    throw new java.io.IOException("boom");
                });

        Map<String, Object> response = aiGatewayService.queryHunyuan("job-404", false);

        assertEquals("FAIL", response.get("Status"));
        assertEquals("job-404", response.get("job_id"));
        assertEquals("QUERY_ERROR", response.get("ErrorCode"));
        server.verify();
    }

    @Test
    void enhanceShouldDefault2KAndReturnUnifiedImageUrl() {
        server.expect(requestTo("http://127.0.0.1:8000/seedream/enhance"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "image_base64": "%s",
                          "size": "2K"
                        }
                        """.formatted(PNG_16), true))
                .andRespond(withSuccess("""
                        {
                          "Status":"DONE",
                          "job_id":null,
                          "ResultFile3Ds":null,
                          "ErrorCode":null,
                          "ErrorMessage":null,
                          "image_url":"https://img.example/result.jpg"
                        }
                        """, MediaType.APPLICATION_JSON));

        AiImageRequest request = new AiImageRequest();
        request.setImage_base64(PNG_16);

        Map<String, Object> response = aiGatewayService.enhance(request);

        assertEquals("DONE", response.get("Status"));
        assertEquals("https://img.example/result.jpg", response.get("image_url"));
        server.verify();
    }

    @Test
    void enhanceShouldPreserveRemoteErrorMessage() {
        server.expect(requestTo("http://127.0.0.1:8000/seedream/enhance"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "success": false,
                          "error": "豆包接口超时"
                        }
                        """, MediaType.APPLICATION_JSON));

        AiImageRequest request = new AiImageRequest();
        request.setImage_base64(PNG_16);

        Map<String, Object> response = aiGatewayService.enhance(request);

        assertEquals("FAIL", response.get("Status"));
        assertEquals("REQUEST_ERROR", response.get("ErrorCode"));
        assertEquals("豆包接口超时", response.get("ErrorMessage"));
        server.verify();
    }

    @Test
    void colorizeByUrlShouldDownloadImageAndProxyBase64ToSeedream() {
        server.expect(requestTo("https://img.example/enhanced.png"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(pngBytes(16, 16), MediaType.IMAGE_PNG));
        server.expect(requestTo("http://127.0.0.1:8000/seedream/colorize"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "image_base64": "%s",
                          "size": "2K"
                        }
                        """.formatted(PNG_16), true))
                .andRespond(withSuccess("""
                        {
                          "Status":"DONE",
                          "job_id":null,
                          "ResultFile3Ds":null,
                          "ErrorCode":null,
                          "ErrorMessage":null,
                          "image_url":"https://img.example/colorized.jpg"
                        }
                        """, MediaType.APPLICATION_JSON));

        Map<String, Object> response = aiGatewayService.colorizeByUrl("https://img.example/enhanced.png");

        assertEquals("DONE", response.get("Status"));
        assertEquals("https://img.example/colorized.jpg", response.get("image_url"));
        server.verify();
    }

    @Test
    void seed3dSubmitShouldProxyToNewAiEndpoint() {
        server.expect(requestTo("http://127.0.0.1:8000/seed3d/submit"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "Status":"WAIT",
                          "job_id":"cgt-test",
                          "ResultFile3Ds":null,
                          "ErrorCode":null,
                          "ErrorMessage":null,
                          "image_url":null
                        }
                        """, MediaType.APPLICATION_JSON));

        AiImageRequest request = new AiImageRequest();
        request.setImage_base64(PNG_16);

        Map<String, Object> response = aiGatewayService.submitSeed3d(request);

        assertEquals("WAIT", response.get("Status"));
        assertEquals("cgt-test", response.get("job_id"));
        server.verify();
    }

    @Test
    void toDataUriShouldEncodeMultipartFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.png",
                MediaType.IMAGE_PNG_VALUE,
                "hello".getBytes(StandardCharsets.UTF_8)
        );

        String dataUri = aiGatewayService.toDataUri(file);

        assertTrue(dataUri.startsWith("data:image/png;base64,"));
    }

    private static String pngDataUri(int width, int height) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngBytes(width, height));
    }

    private static byte[] pngBytes(int width, int height) {
        byte[] bytes = new byte[24];
        bytes[0] = (byte) 0x89;
        bytes[1] = 0x50;
        bytes[2] = 0x4E;
        bytes[3] = 0x47;
        bytes[4] = 0x0D;
        bytes[5] = 0x0A;
        bytes[6] = 0x1A;
        bytes[7] = 0x0A;
        ByteBuffer.wrap(bytes, 16, 4).order(ByteOrder.BIG_ENDIAN).putInt(width);
        ByteBuffer.wrap(bytes, 20, 4).order(ByteOrder.BIG_ENDIAN).putInt(height);
        return bytes;
    }
}
