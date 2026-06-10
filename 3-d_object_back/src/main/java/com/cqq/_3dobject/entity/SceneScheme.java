package com.cqq._3dobject.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "scene_schemes",
        indexes = {
                @Index(name = "idx_scene_schemes_uid_created", columnList = "uid, created_at"),
                @Index(name = "idx_scene_schemes_room_id", columnList = "room_id")
        }
)
public class SceneScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheme_id")
    private Long schemeId;

    @Column(nullable = false)
    private Long uid;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "room_display_name", length = 100)
    private String roomDisplayName;

    @Column(length = 32)
    private String thumb;

    @Column(name = "item_count")
    private Integer itemCount = 0;

    @Column(name = "room_id", nullable = false, length = 32)
    private String roomId;

    @Column(name = "room_name", length = 100)
    private String roomName;

    @Column(length = 32)
    private String shape;

    @Column(length = 20)
    private String area;

    @Column(name = "len_m", precision = 10, scale = 2)
    private BigDecimal lenM;

    @Column(name = "wid_m", precision = 10, scale = 2)
    private BigDecimal widM;

    @Column(name = "height_m", precision = 10, scale = 2)
    private BigDecimal heightM;

    @Column(length = 20)
    private String kind;

    @Column(name = "eave_m", precision = 10, scale = 2)
    private BigDecimal eaveM;

    @Column(name = "dim_line", length = 500)
    private String dimLine;

    @Column(name = "room_data", columnDefinition = "JSON")
    private String roomData;

    @Column(name = "placed_snapshot", columnDefinition = "JSON")
    private String placedSnapshot;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

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

    public String getRoomData() {
        return roomData;
    }

    public void setRoomData(String roomData) {
        this.roomData = roomData;
    }

    public String getPlacedSnapshot() {
        return placedSnapshot;
    }

    public void setPlacedSnapshot(String placedSnapshot) {
        this.placedSnapshot = placedSnapshot;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
