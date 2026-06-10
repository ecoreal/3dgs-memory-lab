package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.SocialAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface SocialActionRepository extends JpaRepository<SocialAction, Long> {

    // 核心查询：判断特定用户对特定模型是否做过特定操作
    Optional<SocialAction> findByUidAndMidAndActionType(Long uid, Long mid, SocialAction.ActionType actionType);

    // 统计数量
    long countByMidAndActionType(Long mid, SocialAction.ActionType actionType);

    // 删除记录（取消操作）
    @Transactional
    void deleteByUidAndMidAndActionType(Long uid, Long mid, SocialAction.ActionType actionType);
}