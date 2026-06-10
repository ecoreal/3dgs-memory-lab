package com.cqq._3dobject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(
        name = "models",
        indexes = {
                @Index(name = "idx_models_uid", columnList = "uid"),
                @Index(name = "idx_models_public_category", columnList = "is_public, category_id"),
                @Index(name = "idx_models_created_at", columnList = "created_at")
        }
)
public class ModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;

    private Long uid; // 所属用户ID

    @Column(name = "category_id")
    private Integer categoryId; // 分类ID

    private String title;

    @Column(columnDefinition = "TEXT")
    private String story;

    @Column(name = "production_year")
    private Integer productionYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "render_status")
    private RenderStatus renderStatus = RenderStatus.waiting;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private List<String> formats = new ArrayList<>();

    // 定义枚举：对应数据库的 ENUM 类型
    public enum RenderStatus {
        waiting, processing, success, failed
    }

    // --- 手写 Getter & Setter ---
    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public RenderStatus getRenderStatus() {
        return renderStatus;
    }

    public void setRenderStatus(RenderStatus renderStatus) {
        this.renderStatus = renderStatus;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }
}
