package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.SceneScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SceneSchemeRepository extends JpaRepository<SceneScheme, Long> {

    List<SceneScheme> findByUidOrderByCreatedAtDesc(Long uid);

    List<SceneScheme> findAllByOrderByCreatedAtDesc();

    Optional<SceneScheme> findBySchemeId(Long schemeId);

    Optional<SceneScheme> findBySchemeIdAndUid(Long schemeId, Long uid);
}
