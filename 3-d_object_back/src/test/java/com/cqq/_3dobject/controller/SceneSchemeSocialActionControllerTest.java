package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.SceneSchemeSocialAction;
import com.cqq._3dobject.service.SceneSchemeSocialActionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SceneSchemeSocialActionControllerTest {

    @Test
    void toggleLikeShouldDelegateWithSchemeId() throws Exception {
        SceneSchemeSocialActionService actionService = mock(SceneSchemeSocialActionService.class);
        when(actionService.toggleAction(7L, 3L, SceneSchemeSocialAction.ActionType.LIKE)).thenReturn("SUCCESS");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new SceneSchemeSocialActionController(actionService)
        ).build();

        mockMvc.perform(post("/api/scene-scheme-actions/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "uid": 7,
                                  "schemeId": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));

        verify(actionService).toggleAction(7L, 3L, SceneSchemeSocialAction.ActionType.LIKE);
    }

    @Test
    void getCountShouldNormalizeTypeToUppercase() throws Exception {
        SceneSchemeSocialActionService actionService = mock(SceneSchemeSocialActionService.class);
        when(actionService.getCount(3L, SceneSchemeSocialAction.ActionType.COLLECT)).thenReturn(2L);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new SceneSchemeSocialActionController(actionService)
        ).build();

        mockMvc.perform(get("/api/scene-scheme-actions/count/3").param("type", "collect"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));

        verify(actionService).getCount(3L, SceneSchemeSocialAction.ActionType.COLLECT);
    }
}
