package com.cqq._3dobject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "model_tags",
        indexes = {
                @Index(name = "idx_model_tags_mid", columnList = "mid"),
                @Index(name = "idx_model_tags_tag_name", columnList = "tag_name")
        }
)
public class ModelTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;

    @Column(nullable = false)
    private Long mid;

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // 构造函数
    public ModelTag() {}

    public ModelTag(Long mid, String tagName) {
        this.mid = mid;
        this.tagName = tagName;
    }

    // --- Getter & Setter ---
    public Long getTid() { return tid; }
    public void setTid(Long tid) { this.tid = tid; }

    public Long getMid() { return mid; }
    public void setMid(Long mid) { this.mid = mid; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
