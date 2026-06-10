package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.ModelTag;
import com.cqq._3dobject.service.ModelTagService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModelTagControllerTest {

    @Test
    void batchAddTagsShouldDelegateMidAndTagNames() throws Exception {
        ModelTagService modelTagService = mock(ModelTagService.class);
        when(modelTagService.addTags(eq(11L), eq(List.of("古风", "青铜"))))
                .thenReturn(List.of(new ModelTag(11L, "古风"), new ModelTag(11L, "青铜")));

        MockMvc mockMvc = buildMockMvc(modelTagService);

        mockMvc.perform(post("/api/model-tags/batch")
                        .param("mid", "11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                ["古风", "青铜"]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mid").value(11))
                .andExpect(jsonPath("$[0].tagName").value("古风"))
                .andExpect(jsonPath("$[1].tagName").value("青铜"));

        verify(modelTagService).addTags(11L, List.of("古风", "青铜"));
    }

    @Test
    void getTagsShouldReturnModelTags() throws Exception {
        ModelTagService modelTagService = mock(ModelTagService.class);
        when(modelTagService.getTagsByMid(11L)).thenReturn(List.of(new ModelTag(11L, "古风")));

        MockMvc mockMvc = buildMockMvc(modelTagService);

        mockMvc.perform(get("/api/model-tags/model/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mid").value(11))
                .andExpect(jsonPath("$[0].tagName").value("古风"));
    }

    private static MockMvc buildMockMvc(ModelTagService modelTagService) {
        ModelTagController controller = new ModelTagController();
        ReflectionTestUtils.setField(controller, "modelTagService", modelTagService);
        return MockMvcBuilders.standaloneSetup(controller).build();
    }
}
