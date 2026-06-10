package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.SceneSchemeSocialAction;
import com.cqq._3dobject.repository.SceneSchemeSocialActionRepository;
import org.springframework.stereotype.Service;

@Service
public class SceneSchemeSocialActionService {

    private final SceneSchemeSocialActionRepository repository;

    public SceneSchemeSocialActionService(SceneSchemeSocialActionRepository repository) {
        this.repository = repository;
    }

    public String toggleAction(Long uid, Long schemeId, SceneSchemeSocialAction.ActionType type) {
        if (repository.findByUidAndSchemeIdAndActionType(uid, schemeId, type).isPresent()) {
            repository.deleteByUidAndSchemeIdAndActionType(uid, schemeId, type);
            return "CANCELLED";
        }
        repository.save(new SceneSchemeSocialAction(uid, schemeId, type));
        return "SUCCESS";
    }

    public long getCount(Long schemeId, SceneSchemeSocialAction.ActionType type) {
        return repository.countBySchemeIdAndActionType(schemeId, type);
    }
}
