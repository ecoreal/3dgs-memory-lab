package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.SceneSchemeComment;
import com.cqq._3dobject.repository.SceneSchemeCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SceneSchemeCommentService {

    private final SceneSchemeCommentRepository repository;

    public SceneSchemeCommentService(SceneSchemeCommentRepository repository) {
        this.repository = repository;
    }

    public SceneSchemeComment addComment(SceneSchemeComment comment) {
        return repository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<SceneSchemeComment> getCommentsByScheme(Long schemeId) {
        return repository.findBySchemeIdOrderByCreatedAtDesc(schemeId);
    }

    public void deleteComment(Long id) {
        repository.deleteById(id);
    }
}
