package com.cqq._3dobject.dto;

import java.math.BigDecimal;

public class ScenePlacementRequest {

    private String placementUid;
    private Long mid;
    private String title;
    private String icon;
    private String modelUrl;
    private BigDecimal normX;
    private BigDecimal normY;
    private BigDecimal rotationY;
    private BigDecimal scale;
    private BigDecimal posY;
    private Integer sortOrder;

    public String getPlacementUid() {
        return placementUid;
    }

    public void setPlacementUid(String placementUid) {
        this.placementUid = placementUid;
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
}
