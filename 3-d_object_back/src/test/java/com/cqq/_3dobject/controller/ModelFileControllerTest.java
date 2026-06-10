package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.ModelFile;
import com.cqq._3dobject.service.ModelFileService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModelFileControllerTest {

    @Test
    void bindFileUrlShouldReturnCreatedModelFileRecord() throws Exception {
        ModelFile record = new ModelFile();
        record.setMid(76L);
        record.setFileType("GLB");
        record.setFilePath("https://ai.example.com/result/model.glb");
        record.setOriginalName("model.glb");
        record.setContentType("model/gltf-binary");

        ModelFileService service = mock(ModelFileService.class);
        when(service.bindFileUrl(
                eq(76L),
                eq("https://ai.example.com/result/model.glb"),
                eq("GLB"),
                eq("model.glb"),
                eq(null),
                eq(null)
        )).thenReturn(record);

        ModelFileController controller = new ModelFileController();
        ReflectionTestUtils.setField(controller, "modelFileService", service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/model-files/bind-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mid": 76,
                                  "fileUrl": "https://ai.example.com/result/model.glb",
                                  "fileType": "GLB",
                                  "originalName": "model.glb"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mid").value(76))
                .andExpect(jsonPath("$.fileType").value("GLB"))
                .andExpect(jsonPath("$.filePath").value("https://ai.example.com/result/model.glb"));
    }
}
