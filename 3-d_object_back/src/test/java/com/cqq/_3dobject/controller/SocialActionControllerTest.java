package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.SocialAction;
import com.cqq._3dobject.service.SocialActionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SocialActionControllerTest {

    @Test
    void toggleLikeShouldDelegateWithLikeType() throws Exception {
        SocialActionService actionService = mock(SocialActionService.class);
        when(actionService.toggleAction(7L, 21L, SocialAction.ActionType.LIKE)).thenReturn("SUCCESS");

        MockMvc mockMvc = buildMockMvc(actionService);

        mockMvc.perform(post("/api/actions/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "uid": 7,
                                  "mid": 21
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));

        verify(actionService).toggleAction(7L, 21L, SocialAction.ActionType.LIKE);
    }

    @Test
    void getCountShouldNormalizeTypeToUppercase() throws Exception {
        SocialActionService actionService = mock(SocialActionService.class);
        when(actionService.getCount(21L, SocialAction.ActionType.COLLECT)).thenReturn(6L);

        MockMvc mockMvc = buildMockMvc(actionService);

        mockMvc.perform(get("/api/actions/count/21").param("type", "collect"))
                .andExpect(status().isOk())
                .andExpect(content().string("6"));

        verify(actionService).getCount(21L, SocialAction.ActionType.COLLECT);
    }

    private static MockMvc buildMockMvc(SocialActionService actionService) {
        SocialActionController controller = new SocialActionController();
        ReflectionTestUtils.setField(controller, "actionService", actionService);
        return MockMvcBuilders.standaloneSetup(controller).build();
    }
}
