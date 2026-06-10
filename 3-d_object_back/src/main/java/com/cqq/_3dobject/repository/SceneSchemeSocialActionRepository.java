package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.SceneSchemeSocialAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SceneSchemeSocialActionRepository extends JpaRepository<SceneSchemeSocialAction, Long> {

    Optional<SceneSchemeSocialAction> findByUidAndSchemeIdAndActionType(
            Long uid,
            Long schemeId,
            SceneSchemeSocialAction.ActionType actionType
    );

    long countBySchemeIdAndActionType(Long schemeId, SceneSchemeSocialAction.ActionType actionType);

    @Transactional
    void deleteByUidAndSchemeIdAndActionType(Long uid, Long schemeId, SceneSchemeSocialAction.ActionType actionType);
}
