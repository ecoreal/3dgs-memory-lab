package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.Comment;
import com.cqq._3dobject.service.CommentService;
import com.cqq._3dobject.service.ViewAssembler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ViewAssembler viewAssembler;

    // 接口：发表评论
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Comment comment, HttpServletRequest request) {
        Long currentUid = (Long) request.getAttribute("current_uid");
        if (currentUid != null) {
            comment.setUid(currentUid);
        }
        Comment created = commentService.addComment(comment);
        return viewAssembler.toCommentView(created);
    }

    // 接口：获取模型下的评论列表
    @GetMapping("/model/{mid}")
    public List<Map<String, Object>> list(@PathVariable("mid") Long mid) {
        return viewAssembler.toCommentViews(commentService.getCommentsByModel(mid));
    }

    // 接口：删除评论
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return "SUCCESS";
    }
}
