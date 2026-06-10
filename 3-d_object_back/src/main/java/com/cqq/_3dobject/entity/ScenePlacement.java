package com.cqq._3dobject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "scene_placements",
        indexes = {
                @Index(name = "idx_scene_placements_scheme_sort", columnList = "scheme_id, sort_order"),
                @Index(name = "idx_scene_placements_mid", columnList = "mid")
        }
)
public class ScenePlacement {

    @Id
    @Column(name = "placement_uid", length = 64)
    private String placementUid;

    @Column(name = "scheme_id", nullable = false)
    private Long schemeId;

    @Column(nullable = false)
    private Long mid;

    @Column(length = 100)
    private String title;

    @Column(length = 32)
    private String icon;

    @Column(name = "model_url", columnDefinition = "TEXT")
    private String modelUrl;

    @Column(name = "norm_x", nullable = false, precision = 5, scale = 2)
    private BigDecimal normX;

    @Column(name = "norm_y", nullable = false, precision = 5, scale = 2)
    private BigDecimal normY;

    @Column(name = "rotation_y", precision = 10, scale = 4)
    private BigDecimal rotationY;

    @Column(precision = 10, scale = 4)
    private BigDecimal scale;

    @Column(name = "pos_y", precision = 10, scale = 4)
    private BigDecimal posY;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public String getPlacementUid() {
        return placementUid;
    }

    public void setPlacementUid(String placementUid) {
        this.placementUid = placementUid;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public BigDecimal getNormX() {
        return normX;
    }

    public void setNormX(BigDecimal normX) {
        this.normX = normX;
    }

    public BigDecimal getNormY() {
        return normY;
    }

    public void setNormY(BigDecimal normY) {
        this.normY = normY;
    }

    public BigDecimal getRotationY() {
        return rotationY;
    }

    public void setRotationY(BigDecimal rotationY) {
        this.rotationY = rotationY;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public BigDecimal getPosY() {
        return posY;
    }

    public void setPosY(BigDecimal posY) {
        this.posY = posY;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
