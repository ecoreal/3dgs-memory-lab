package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.SocialAction;
import com.cqq._3dobject.repository.SocialActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialActionService {

    @Autowired
    private SocialActionRepository repository;

    // 通用切换逻辑：已存在则删除，不存在则保存
    public String toggleAction(Long uid, Long mid, SocialAction.ActionType type) {
        if (repository.findByUidAndMidAndActionType(uid, mid, type).isPresent()) {
            repository.deleteByUidAndMidAndActionType(uid, mid, type);
            return "CANCELLED";
        }
        repository.save(new SocialAction(uid, mid, type));
        return "SUCCESS";
    }

    public long getCount(Long mid, SocialAction.ActionType type) {
        return repository.countByMidAndActionType(mid, type);
    }
}