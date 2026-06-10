package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.SceneSchemeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SceneSchemeCommentRepository extends JpaRepository<SceneSchemeComment, Long> {

    List<SceneSchemeComment> findBySchemeIdOrderByCreatedAtDesc(Long schemeId);

    long countBySchemeId(Long schemeId);
}
