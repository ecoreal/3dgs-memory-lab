package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.Comment;
import com.cqq._3dobject.service.CommentService;
import com.cqq._3dobject.service.ViewAssembler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {

    @Test
    void addShouldOverrideUidFromCurrentUser() throws Exception {
        CommentService commentService = mock(CommentService.class);
        ViewAssembler viewAssembler = mock(ViewAssembler.class);
        when(commentService.addComment(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(15L);
            return comment;
        });
        when(viewAssembler.toCommentView(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    return Map.of("id", comment.getId(), "uid", comment.getUid(), "content", comment.getContent());
                });

        MockMvc mockMvc = buildMockMvc(commentService, viewAssembler);

        mockMvc.perform(post("/api/comments/add")
                        .requestAttr("current_uid", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mid": 21,
                                  "uid": 999,
                                  "content": "很不错"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.uid").value(7))
                .andExpect(jsonPath("$.content").value("很不错"));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentService).addComment(captor.capture());
        assertEquals(7L, captor.getValue().getUid());
    }

    @Test
    void listShouldReturnAssemblerViewList() throws Exception {
        CommentService commentService = mock(CommentService.class);
        ViewAssembler viewAssembler = mock(ViewAssembler.class);
        when(commentService.getCommentsByModel(21L)).thenReturn(List.of(new Comment()));
        when(viewAssembler.toCommentViews(any())).thenReturn(List.of(Map.of("id", 15L, "nickname", "Alice")));

        MockMvc mockMvc = buildMockMvc(commentService, viewAssembler);

        mockMvc.perform(get("/api/comments/model/21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(15))
                .andExpect(jsonPath("$[0].nickname").value("Alice"));
    }

    @Test
    void deleteShouldReturnSuccessString() throws Exception {
        CommentService commentService = mock(CommentService.class);
        ViewAssembler viewAssembler = mock(ViewAssembler.class);

        MockMvc mockMvc = buildMockMvc(commentService, viewAssembler);

        mockMvc.perform(delete("/api/comments/15"))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));

        verify(commentService).deleteComment(15L);
    }

    private static MockMvc buildMockMvc(CommentService commentService, ViewAssembler viewAssembler) {
        CommentController controller = new CommentController();
        ReflectionTestUtils.setField(controller, "commentService", commentService);
        ReflectionTestUtils.setField(controller, "viewAssembler", viewAssembler);
        return MockMvcBuilders.standaloneSetup(controller).build();
    }
}
