package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.SceneSchemeRequest;
import com.cqq._3dobject.service.SceneSchemeService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SceneSchemeControllerTest {

    @Test
    void listMineShouldUseCurrentUidFromRequest() throws Exception {
        SceneSchemeService service = mock(SceneSchemeService.class);
        when(service.listMine(7L)).thenReturn(List.of(Map.of("schemeId", 88L, "title", "中型展厅")));

        MockMvc mockMvc = buildMockMvc(service);

        mockMvc.perform(get("/api/scene-schemes/mine").requestAttr("current_uid", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].schemeId").value(88))
                .andExpect(jsonPath("$[0].title").value("中型展厅"));

        verify(service).listMine(7L);
    }

    @Test
    void createShouldReturnBadRequestWhenRoomIdMissing() throws Exception {
        SceneSchemeService service = mock(SceneSchemeService.class);
        when(service.create(eq(7L), any(SceneSchemeRequest.class))).thenThrow(new IllegalArgumentException("roomId 不能为空"));

        MockMvc mockMvc = buildMockMvc(service);

        mockMvc.perform(post("/api/scene-schemes")
                        .requestAttr("current_uid", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "测试方案"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("roomId 不能为空"));
    }

    @Test
    void listPublicShouldDelegateKeywordAndRoomId() throws Exception {
        SceneSchemeService service = mock(SceneSchemeService.class);
        when(service.listPublic("展馆", "medium")).thenReturn(List.of(Map.of("schemeId", 3L, "title", "中型展馆")));

        MockMvc mockMvc = buildMockMvc(service);

        mockMvc.perform(get("/api/scene-schemes/public")
                        .param("keyword", "展馆")
                        .param("roomId", "medium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].schemeId").value(3))
                .andExpect(jsonPath("$[0].title").value("中型展馆"));

        verify(service).listPublic("展馆", "medium");
    }

    @Test
    void publicDetailShouldReturnNotFoundWhenMissing() throws Exception {
        SceneSchemeService service = mock(SceneSchemeService.class);
        when(service.getPublicDetail(99L)).thenThrow(new RuntimeException("方案不存在"));

        MockMvc mockMvc = buildMockMvc(service);

        mockMvc.perform(get("/api/scene-schemes/public/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("方案不存在"));
    }

    @Test
    void deleteShouldReturnNotFoundForMissingScheme() throws Exception {
        SceneSchemeService service = mock(SceneSchemeService.class);
        org.mockito.Mockito.doThrow(new RuntimeException("方案不存在或无权限访问"))
                .when(service).delete(7L, 88L);

        MockMvc mockMvc = buildMockMvc(service);

        mockMvc.perform(delete("/api/scene-schemes/88").requestAttr("current_uid", 7L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("方案不存在或无权限访问"));
    }

    private static MockMvc buildMockMvc(SceneSchemeService service) {
        return MockMvcBuilders.standaloneSetup(new SceneSchemeController(service)).build();
    }
}
