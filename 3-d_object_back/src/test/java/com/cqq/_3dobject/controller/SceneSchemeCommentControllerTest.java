package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.SceneSchemeComment;
import com.cqq._3dobject.service.SceneSchemeCommentService;
import com.cqq._3dobject.service.ViewAssembler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SceneSchemeCommentControllerTest {

    @Test
    void addShouldOverrideUidFromCurrentUser() throws Exception {
        SceneSchemeCommentService commentService = mock(SceneSchemeCommentService.class);
        ViewAssembler viewAssembler = mock(ViewAssembler.class);
        when(commentService.addComment(any(SceneSchemeComment.class))).thenAnswer(invocation -> {
            SceneSchemeComment comment = invocation.getArgument(0);
            comment.setId(16L);
            return comment;
        });
        when(viewAssembler.toSceneSchemeCommentView(any(SceneSchemeComment.class)))
                .thenAnswer(invocation -> {
                    SceneSchemeComment comment = invocation.getArgument(0);
                    return Map.of("id", comment.getId(), "uid", comment.getUid(), "schemeId", comment.getSchemeId());
                });

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new SceneSchemeCommentController(commentService, viewAssembler)
        ).build();

        mockMvc.perform(post("/api/scene-scheme-comments/add")
                        .requestAttr("current_uid", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "schemeId": 3,
                                  "uid": 999,
                                  "content": "展馆不错"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(16))
                .andExpect(jsonPath("$.uid").value(7))
                .andExpect(jsonPath("$.schemeId").value(3));

        ArgumentCaptor<SceneSchemeComment> captor = ArgumentCaptor.forClass(SceneSchemeComment.class);
        verify(commentService).addComment(captor.capture());
        assertEquals(7L, captor.getValue().getUid());
    }

    @Test
    void listAndDeleteShouldDelegateToService() throws Exception {
        SceneSchemeCommentService commentService = mock(SceneSchemeCommentService.class);
        ViewAssembler viewAssembler = mock(ViewAssembler.class);
        when(commentService.getCommentsByScheme(3L)).thenReturn(List.of(new SceneSchemeComment()));
        when(viewAssembler.toSceneSchemeCommentViews(any())).thenReturn(List.of(Map.of("id", 16L, "nickname", "Bob")));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new SceneSchemeCommentController(commentService, viewAssembler)
        ).build();

        mockMvc.perform(get("/api/scene-scheme-comments/scheme/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(16))
                .andExpect(jsonPath("$[0].nickname").value("Bob"));

        mockMvc.perform(delete("/api/scene-scheme-comments/16"))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));

        verify(commentService).deleteComment(16L);
    }
}
