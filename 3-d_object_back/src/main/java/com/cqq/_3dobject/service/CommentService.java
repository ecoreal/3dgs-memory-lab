package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.Comment;
import com.cqq._3dobject.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 发表评论
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 获取评论列表
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByModel(Long mid) {
        return commentRepository.findByMidOrderByCreatedAtDesc(mid);
    }

    // 删除评论（仅限本人或管理员，此处先实现基础删除）
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
