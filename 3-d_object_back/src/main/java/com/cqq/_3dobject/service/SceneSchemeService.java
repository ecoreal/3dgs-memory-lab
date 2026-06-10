package com.cqq._3dobject.service;

import com.cqq._3dobject.dto.ScenePlacementRequest;
import com.cqq._3dobject.dto.SceneSchemeRequest;
import com.cqq._3dobject.entity.ModelEntity;
import com.cqq._3dobject.entity.ModelFile;
import com.cqq._3dobject.entity.ScenePlacement;
import com.cqq._3dobject.entity.SceneScheme;
import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.repository.ModelFileRepository;
import com.cqq._3dobject.repository.ModelRepository;
import com.cqq._3dobject.repository.ScenePlacementRepository;
import com.cqq._3dobject.repository.SceneSchemeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SceneSchemeService {

    private final SceneSchemeRepository sceneSchemeRepository;
    private final ScenePlacementRepository scenePlacementRepository;
    private final ModelRepository modelRepository;
    private final ModelFileRepository modelFileRepository;
    private final ModelFileService modelFileService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public SceneSchemeService(SceneSchemeRepository sceneSchemeRepository,
                              ScenePlacementRepository scenePlacementRepository,
                              ModelRepository modelRepository,
                              ModelFileRepository modelFileRepository,
                              ModelFileService modelFileService,
                              UserService userService,
                              ObjectMapper objectMapper) {
        this.sceneSchemeRepository = sceneSchemeRepository;
        this.scenePlacementRepository = scenePlacementRepository;
        this.modelRepository = modelRepository;
        this.modelFileRepository = modelFileRepository;
        this.modelFileService = modelFileService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMine(Long uid) {
        return sceneSchemeRepository.findByUidOrderByCreatedAtDesc(uid).stream()
                .map(this::toSummaryView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listPublic(String keyword, String roomId) {
        String normalizedKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        String normalizedRoomId = roomId != null ? roomId.trim() : "";

        List<SceneScheme> schemes = sceneSchemeRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(scheme -> !StringUtils.hasText(normalizedRoomId) || normalizedRoomId.equalsIgnoreCase(scheme.getRoomId()))
                .filter(scheme -> matchesKeyword(scheme, normalizedKeyword))
                .toList();

        Map<Long, User> ownersById = userService.getUserMapByIds(
                schemes.stream().map(SceneScheme::getUid).filter(Objects::nonNull).distinct().toList()
        );

        return schemes.stream()
                .map(scheme -> toPublicSummaryView(scheme, ownersById.get(scheme.getUid())))
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDetail(Long uid, Long schemeId) {
        SceneScheme scheme = requireOwnedScheme(uid, schemeId);
        List<ScenePlacement> placements = scenePlacementRepository.findBySchemeIdOrderBySortOrderAscCreatedAtAsc(schemeId);
        return toDetailView(scheme, placements);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPublicDetail(Long schemeId) {
        SceneScheme scheme = sceneSchemeRepository.findBySchemeId(schemeId)
                .orElseThrow(() -> new RuntimeException("方案不存在"));
        List<ScenePlacement> placements = scenePlacementRepository.findBySchemeIdOrderBySortOrderAscCreatedAtAsc(schemeId);
        return toDetailView(scheme, placements);
    }

    @Transactional
    public Map<String, Object> create(Long uid, SceneSchemeRequest request) {
        SceneScheme scheme = new SceneScheme();
        scheme.setUid(uid);
        return save(uid, scheme, request);
    }

    @Transactional
    public Map<String, Object> update(Long uid, Long schemeId, SceneSchemeRequest request) {
        SceneScheme scheme = requireOwnedScheme(uid, schemeId);
        return save(uid, scheme, request);
    }

    @Transactional
    public void delete(Long uid, Long schemeId) {
        SceneScheme scheme = requireOwnedScheme(uid, schemeId);
        scenePlacementRepository.deleteBySchemeId(scheme.getSchemeId());
        sceneSchemeRepository.delete(scheme);
    }

    private Map<String, Object> save(Long uid, SceneScheme scheme, SceneSchemeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }

        Map<String, Object> roomData = new LinkedHashMap<>();
        Object existingRoomData = readJson(scheme.getRoomData());
        if (existingRoomData instanceof Map<?, ?> existingMap) {
            existingMap.forEach((key, value) -> roomData.put(String.valueOf(key), value));
        }
        if (request.getRoomData() != null) {
            roomData.putAll(request.getRoomData());
        }

        String roomId = firstText(request.getRoomId(), roomData.get("id"), scheme.getRoomId());
        if (!StringUtils.hasText(roomId)) {
            throw new IllegalArgumentException("roomId 不能为空");
        }

        List<ScenePlacementRequest> placementRequests = request.getPlacements() != null
                ? request.getPlacements()
                : List.of();

        Map<Long, ModelEntity> modelsById = loadModels(placementRequests.stream()
                .map(ScenePlacementRequest::getMid)
                .filter(Objects::nonNull)
                .toList());
        validateModelOwnership(uid, modelsById.values());

        Map<Long, String> modelUrls = loadPreviewModelUrls(modelsById.keySet());
        List<ScenePlacement> placements = materializePlacements(placementRequests, modelsById, modelUrls);

        enrichRoomData(roomData, request);

        String roomDisplayName = firstText(request.getRoomDisplayName(), roomData.get("name"), request.getRoomName(), scheme.getRoomDisplayName(), scheme.getRoomName());
        String schemeTitle = StringUtils.hasText(request.getTitle())
                ? request.getTitle().trim()
                : firstText(scheme.getTitle(), buildDefaultTitle(roomDisplayName));

        scheme.setTitle(schemeTitle);
        scheme.setDescription(firstText(request.getDescription(), scheme.getDescription()));
        scheme.setCoverImageUrl(firstText(request.getCoverImageUrl(), scheme.getCoverImageUrl()));
        scheme.setRoomDisplayName(roomDisplayName);
        scheme.setRoomId(roomId.trim());
        scheme.setRoomName(firstText(request.getRoomName(), roomData.get("name"), scheme.getRoomName()));
        scheme.setShape(firstText(request.getShape(), roomData.get("shape"), scheme.getShape()));
        scheme.setArea(firstText(request.getArea(), roomData.get("area"), scheme.getArea()));
        scheme.setKind(firstText(request.getKind(), roomData.get("kind"), scheme.getKind()));
        scheme.setDimLine(firstText(request.getDimLine(), roomData.get("dimLine"), scheme.getDimLine()));
        scheme.setLenM(firstDecimal(request.getLenM(), roomData.get("lenM"), scheme.getLenM()));
        scheme.setWidM(firstDecimal(request.getWidM(), roomData.get("widM"), scheme.getWidM()));
        scheme.setHeightM(firstDecimal(request.getHeightM(), roomData.get("heightM"), scheme.getHeightM()));
        scheme.setEaveM(firstDecimal(request.getEaveM(), roomData.get("eaveM"), scheme.getEaveM()));
        scheme.setItemCount(request.getItemCount() != null ? request.getItemCount() : placements.size());
        scheme.setThumb(resolveThumb(request.getThumb(), placements));
        scheme.setRoomData(writeJson(roomData));
        scheme.setPlacedSnapshot(writeJson(resolvePlacedSnapshot(request, placements)));

        SceneScheme saved = sceneSchemeRepository.save(scheme);

        scenePlacementRepository.deleteBySchemeId(saved.getSchemeId());
        for (ScenePlacement placement : placements) {
            placement.setSchemeId(saved.getSchemeId());
        }
        List<ScenePlacement> savedPlacements = scenePlacementRepository.saveAll(placements);
        return toDetailView(saved, savedPlacements);
    }

    private SceneScheme requireOwnedScheme(Long uid, Long schemeId) {
        return sceneSchemeRepository.findBySchemeIdAndUid(schemeId, uid)
                .orElseThrow(() -> new RuntimeException("方案不存在或无权限访问"));
    }

    private Map<Long, ModelEntity> loadModels(Collection<Long> mids) {
        if (mids == null || mids.isEmpty()) {
            return Map.of();
        }
        return modelRepository.findAllById(mids).stream()
                .collect(Collectors.toMap(ModelEntity::getMid, Function.identity()));
    }

    private void validateModelOwnership(Long uid, Collection<ModelEntity> models) {
        for (ModelEntity model : models) {
            if (model == null) {
                continue;
            }
            if (!Objects.equals(uid, model.getUid())) {
                throw new RuntimeException("存在不属于当前用户的藏品，无法保存方案");
            }
        }
    }

    private Map<Long, String> loadPreviewModelUrls(Collection<Long> mids) {
        if (mids == null || mids.isEmpty()) {
            return Map.of();
        }

        return modelFileRepository.findByMidIn(mids).stream()
                .sorted(Comparator
                        .comparing((ModelFile file) -> fileTypeRank(file.getFileType()))
                        .thenComparing(ModelFile::getId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toMap(
                        ModelFile::getMid,
                        modelFileService::buildAccessUrl,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private int fileTypeRank(String fileType) {
        if (!StringUtils.hasText(fileType)) {
            return 99;
        }
        String normalized = fileType.trim().toUpperCase();
        if ("GLB".equals(normalized)) {
            return 0;
        }
        if ("GLTF".equals(normalized)) {
            return 1;
        }
        return 10;
    }

    private List<ScenePlacement> materializePlacements(List<ScenePlacementRequest> requests,
                                                       Map<Long, ModelEntity> modelsById,
                                                       Map<Long, String> modelUrls) {
        List<ScenePlacement> placements = new ArrayList<>();
        for (int index = 0; index < requests.size(); index++) {
            ScenePlacementRequest request = requests.get(index);
            if (request == null) {
                continue;
            }
            if (request.getMid() == null) {
                throw new IllegalArgumentException("placement.mid 不能为空");
            }
            if (request.getNormX() == null || request.getNormY() == null) {
                throw new IllegalArgumentException("placement.normX 和 placement.normY 不能为空");
            }

            ModelEntity model = Optional.ofNullable(modelsById.get(request.getMid()))
                    .orElseThrow(() -> new RuntimeException("藏品不存在: " + request.getMid()));

            ScenePlacement placement = new ScenePlacement();
            placement.setPlacementUid(StringUtils.hasText(request.getPlacementUid())
                    ? request.getPlacementUid().trim()
                    : UUID.randomUUID().toString());
            placement.setMid(request.getMid());
            placement.setTitle(firstText(request.getTitle(), model.getTitle()));
            placement.setIcon(request.getIcon());
            placement.setModelUrl(firstText(request.getModelUrl(), modelUrls.get(request.getMid())));
            placement.setNormX(request.getNormX());
            placement.setNormY(request.getNormY());
            placement.setRotationY(request.getRotationY());
            placement.setScale(request.getScale());
            placement.setPosY(request.getPosY());
            placement.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : index);
            placements.add(placement);
        }
        return placements;
    }

    private String resolveThumb(String explicitThumb, List<ScenePlacement> placements) {
        if (StringUtils.hasText(explicitThumb)) {
            return explicitThumb.trim();
        }
        return placements.stream()
                .map(ScenePlacement::getIcon)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse("scene");
    }

    private Object resolvePlacedSnapshot(SceneSchemeRequest request, List<ScenePlacement> placements) {
        if (request.getPlacedSnapshot() != null) {
            return request.getPlacedSnapshot();
        }
        return placements.stream().map(this::toPlacementView).toList();
    }

    private void enrichRoomData(Map<String, Object> roomData, SceneSchemeRequest request) {
        putIfMissing(roomData, "id", request.getRoomId());
        putIfMissing(roomData, "name", request.getRoomName());
        putIfMissing(roomData, "shape", request.getShape());
        putIfMissing(roomData, "area", request.getArea());
        putIfMissing(roomData, "kind", request.getKind());
        putIfMissing(roomData, "dimLine", request.getDimLine());
        putIfMissing(roomData, "lenM", request.getLenM());
        putIfMissing(roomData, "widM", request.getWidM());
        putIfMissing(roomData, "heightM", request.getHeightM());
        putIfMissing(roomData, "eaveM", request.getEaveM());
    }

    private void putIfMissing(Map<String, Object> target, String key, Object value) {
        if (!target.containsKey(key) && value != null) {
            target.put(key, value);
        }
    }

    private Map<String, Object> toSummaryView(SceneScheme scheme) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("schemeId", scheme.getSchemeId());
        result.put("uid", scheme.getUid());
        result.put("title", scheme.getTitle());
        result.put("description", scheme.getDescription());
        result.put("coverImageUrl", scheme.getCoverImageUrl());
        result.put("roomDisplayName", scheme.getRoomDisplayName());
        result.put("thumb", scheme.getThumb());
        result.put("itemCount", scheme.getItemCount());
        result.put("roomId", scheme.getRoomId());
        result.put("roomName", scheme.getRoomName());
        result.put("shape", scheme.getShape());
        result.put("area", scheme.getArea());
        result.put("kind", scheme.getKind());
        result.put("createdAt", scheme.getCreatedAt());
        return result;
    }

    private Map<String, Object> toPublicSummaryView(SceneScheme scheme, User owner) {
        Map<String, Object> result = new LinkedHashMap<>(toSummaryView(scheme));
        if (owner != null) {
            result.put("nickname", owner.getNickname());
            result.put("avatarUrl", owner.getAvatarUrl());
        }

        List<ScenePlacement> placements = scenePlacementRepository.findBySchemeIdOrderBySortOrderAscCreatedAtAsc(scheme.getSchemeId());
        result.put("placements", placements.stream().map(this::toPlacementView).toList());
        result.put("placementCount", placements.size());

        if (!placements.isEmpty()) {
            ScenePlacement firstPlacement = placements.get(0);
            result.put("coverModelUrl", firstPlacement.getModelUrl());
            result.put("coverTitle", firstPlacement.getTitle());

            ModelEntity model = modelRepository.findById(firstPlacement.getMid()).orElse(null);
            if (model != null) {
                result.put("coverImageUrl", firstText(scheme.getCoverImageUrl(), model.getThumbnailUrl()));
                result.put("description", firstText(scheme.getDescription(), model.getStory()));
            }
        } else {
            result.put("coverImageUrl", scheme.getCoverImageUrl());
            result.put("description", scheme.getDescription());
        }

        return result;
    }

    private Map<String, Object> toDetailView(SceneScheme scheme, List<ScenePlacement> placements) {
        Map<String, Object> result = new LinkedHashMap<>(toSummaryView(scheme));
        result.put("lenM", scheme.getLenM());
        result.put("widM", scheme.getWidM());
        result.put("heightM", scheme.getHeightM());
        result.put("eaveM", scheme.getEaveM());
        result.put("dimLine", scheme.getDimLine());
        result.put("roomData", readJson(scheme.getRoomData()));
        result.put("placedSnapshot", readJson(scheme.getPlacedSnapshot()));
        result.put("placements", placements.stream().map(this::toPlacementView).toList());
        return result;
    }

    private boolean matchesKeyword(SceneScheme scheme, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }

        return containsIgnoreCase(scheme.getTitle(), keyword)
                || containsIgnoreCase(scheme.getDescription(), keyword)
                || containsIgnoreCase(scheme.getRoomDisplayName(), keyword)
                || containsIgnoreCase(scheme.getRoomName(), keyword)
                || containsIgnoreCase(scheme.getArea(), keyword)
                || containsIgnoreCase(scheme.getKind(), keyword);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return StringUtils.hasText(value) && value.toLowerCase().contains(keyword);
    }

    private Map<String, Object> toPlacementView(ScenePlacement placement) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("placementUid", placement.getPlacementUid());
        result.put("schemeId", placement.getSchemeId());
        result.put("mid", placement.getMid());
        result.put("title", placement.getTitle());
        result.put("icon", placement.getIcon());
        result.put("modelUrl", placement.getModelUrl());
        result.put("normX", placement.getNormX());
        result.put("normY", placement.getNormY());
        result.put("rotationY", placement.getRotationY());
        result.put("scale", placement.getScale());
        result.put("posY", placement.getPosY());
        result.put("sortOrder", placement.getSortOrder());
        result.put("createdAt", placement.getCreatedAt());
        return result;
    }

    private Object readJson(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return json;
        }
    }

    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    private String buildDefaultTitle(String roomDisplayName) {
        String prefix = StringUtils.hasText(roomDisplayName) ? roomDisplayName.trim() : "未命名展馆";
        return prefix + "-" + LocalDateTime.now().withNano(0);
    }

    private String firstText(Object... values) {
        for (Object value : values) {
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value).trim();
            }
        }
        return null;
    }

    private BigDecimal firstDecimal(BigDecimal explicitValue, Object... fallbacks) {
        if (explicitValue != null) {
            return explicitValue;
        }
        for (Object fallback : fallbacks) {
            if (fallback == null) {
                continue;
            }
            if (fallback instanceof BigDecimal decimal) {
                return decimal;
            }
            try {
                return new BigDecimal(String.valueOf(fallback));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
