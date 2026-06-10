package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.SceneSchemeComment;
import com.cqq._3dobject.service.SceneSchemeCommentService;
import com.cqq._3dobject.service.ViewAssembler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scene-scheme-comments")
public class SceneSchemeCommentController {

    private final SceneSchemeCommentService commentService;
    private final ViewAssembler viewAssembler;

    public SceneSchemeCommentController(SceneSchemeCommentService commentService, ViewAssembler viewAssembler) {
        this.commentService = commentService;
        this.viewAssembler = viewAssembler;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SceneSchemeComment comment, HttpServletRequest request) {
        Long currentUid = (Long) request.getAttribute("current_uid");
        if (currentUid != null) {
            comment.setUid(currentUid);
        }
        SceneSchemeComment created = commentService.addComment(comment);
        return viewAssembler.toSceneSchemeCommentView(created);
    }

    @GetMapping("/scheme/{schemeId}")
    public List<Map<String, Object>> list(@PathVariable("schemeId") Long schemeId) {
        return viewAssembler.toSceneSchemeCommentViews(commentService.getCommentsByScheme(schemeId));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return "SUCCESS";
    }
}
