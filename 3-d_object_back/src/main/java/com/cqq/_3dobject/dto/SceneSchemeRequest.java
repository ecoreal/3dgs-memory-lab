package com.cqq._3dobject.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SceneSchemeRequest {

    private String title;
    private String description;
    private String coverImageUrl;
    private String roomDisplayName;
    private String thumb;
    private Integer itemCount;
    private String roomId;
    private String roomName;
    private String shape;
    private String area;
    private BigDecimal lenM;
    private BigDecimal widM;
    private BigDecimal heightM;
    private String kind;
    private BigDecimal eaveM;
    private String dimLine;
    private Map<String, Object> roomData;
    private Object placedSnapshot;
    private List<ScenePlacementRequest> placements;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getRoomDisplayName() {
        return roomDisplayName;
    }

    public void setRoomDisplayName(String roomDisplayName) {
        this.roomDisplayName = roomDisplayName;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public BigDecimal getLenM() {
        return lenM;
    }

    public void setLenM(BigDecimal lenM) {
        this.lenM = lenM;
    }

    public BigDecimal getWidM() {
        return widM;
    }

    public void setWidM(BigDecimal widM) {
        this.widM = widM;
    }

    public BigDecimal getHeightM() {
        return heightM;
    }

    public void setHeightM(BigDecimal heightM) {
        this.heightM = heightM;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public BigDecimal getEaveM() {
        return eaveM;
    }

    public void setEaveM(BigDecimal eaveM) {
        this.eaveM = eaveM;
    }

    public String getDimLine() {
        return dimLine;
    }

    public void setDimLine(String dimLine) {
        this.dimLine = dimLine;
    }

    public Map<String, Object> getRoomData() {
        return roomData;
    }

    public void setRoomData(Map<String, Object> roomData) {
        this.roomData = roomData;
    }

    public Object getPlacedSnapshot() {
        return placedSnapshot;
    }

    public void setPlacedSnapshot(Object placedSnapshot) {
        this.placedSnapshot = placedSnapshot;
    }

    public List<ScenePlacementRequest> getPlacements() {
        return placements;
    }

    public void setPlacements(List<ScenePlacementRequest> placements) {
        this.placements = placements;
    }
}
