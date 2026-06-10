package com.cqq._3dobject.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "scene_scheme_social_actions",
        indexes = {
                @Index(name = "idx_scene_scheme_social_actions_scheme_type", columnList = "scheme_id, action_type"),
                @Index(name = "idx_scene_scheme_social_actions_uid_scheme_type", columnList = "uid, scheme_id, action_type")
        }
)
public class SceneSchemeSocialAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long uid;

    @Column(name = "scheme_id", nullable = false)
    private Long schemeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ActionType {
        LIKE, COLLECT
    }

    public SceneSchemeSocialAction() {
    }

    public SceneSchemeSocialAction(Long uid, Long schemeId, ActionType actionType) {
        this.uid = uid;
        this.schemeId = schemeId;
        this.actionType = actionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
