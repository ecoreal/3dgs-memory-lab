package com.cqq._3dobject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_actions")
public class SocialAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long uid;
    private Long mid;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // 枚举定义
    public enum ActionType {
        LIKE, COLLECT
    }

    public SocialAction() {}

    public SocialAction(Long uid, Long mid, ActionType actionType) {
        this.uid = uid;
        this.mid = mid;
        this.actionType = actionType;
    }

    // --- Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
    public Long getMid() { return mid; }
    public void setMid(Long mid) { this.mid = mid; }
    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}