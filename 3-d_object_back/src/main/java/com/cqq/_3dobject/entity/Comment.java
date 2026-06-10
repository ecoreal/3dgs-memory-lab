package com.cqq._3dobject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_mid_created", columnList = "mid, created_at"),
                @Index(name = "idx_comments_uid", columnList = "uid")
        }
)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mid; // 模型ID

    @Column(nullable = false)
    private Long uid; // 用户ID

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 评论内容

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- 构造函数 ---
    public Comment() {}

    // --- Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMid() { return mid; }
    public void setMid(Long mid) { this.mid = mid; }

    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
