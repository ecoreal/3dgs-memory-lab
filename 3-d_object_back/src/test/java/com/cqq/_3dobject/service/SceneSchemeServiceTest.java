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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SceneSchemeServiceTest {

    @Test
    void createShouldDerivePlacementModelUrlAndPersistSnapshots() {
        SceneSchemeRepository sceneSchemeRepository = mock(SceneSchemeRepository.class);
        ScenePlacementRepository scenePlacementRepository = mock(ScenePlacementRepository.class);
        ModelRepository modelRepository = mock(ModelRepository.class);
        ModelFileRepository modelFileRepository = mock(ModelFileRepository.class);
        ModelFileService modelFileService = mock(ModelFileService.class);
        UserService userService = mock(UserService.class);

        ModelEntity model = new ModelEntity();
        model.setMid(21L);
        model.setUid(7L);
        model.setTitle("青铜器");
        when(modelRepository.findAllById(List.of(21L))).thenReturn(List.of(model));

        ModelFile modelFile = new ModelFile();
        modelFile.setId(3L);
        modelFile.setMid(21L);
        modelFile.setFileType("GLB");
        modelFile.setFilePath("uploads/models/21/bronze.glb");
        when(modelFileRepository.findByMidIn(List.of(21L))).thenReturn(List.of(modelFile));
        when(modelFileService.buildAccessUrl(modelFile)).thenReturn("http://localhost:8080/uploads/models/21/bronze.glb");
        when(userService.getUserMapByIds(any())).thenReturn(Map.of());

        when(sceneSchemeRepository.save(any(SceneScheme.class))).thenAnswer(invocation -> {
            SceneScheme saved = invocation.getArgument(0);
            saved.setSchemeId(88L);
            return saved;
        });
        when(scenePlacementRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SceneSchemeService service = new SceneSchemeService(
                sceneSchemeRepository,
                scenePlacementRepository,
                modelRepository,
                modelFileRepository,
                modelFileService,
                userService,
                new ObjectMapper()
        );

        ScenePlacementRequest placement = new ScenePlacementRequest();
        placement.setMid(21L);
        placement.setNormX(new BigDecimal("12.50"));
        placement.setNormY(new BigDecimal("68.00"));
        placement.setIcon("bronze");

        SceneSchemeRequest request = new SceneSchemeRequest();
        request.setRoomId("medium");
        request.setDescription("展馆简介");
        request.setCoverImageUrl("http://localhost:8080/uploads/scene-cover/cover.jpg");
        request.setRoomDisplayName("中型展厅");
        request.setRoomData(Map.of("id", "medium", "name", "中型展厅", "shape", "flat", "lenM", 8));
        request.setPlacements(List.of(placement));

        Map<String, Object> response = service.create(7L, request);

        ArgumentCaptor<SceneScheme> schemeCaptor = ArgumentCaptor.forClass(SceneScheme.class);
        verify(sceneSchemeRepository).save(schemeCaptor.capture());
        SceneScheme savedScheme = schemeCaptor.getValue();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ScenePlacement>> placementCaptor = ArgumentCaptor.forClass(List.class);
        verify(scenePlacementRepository).saveAll(placementCaptor.capture());
        ScenePlacement savedPlacement = placementCaptor.getValue().get(0);

        assertEquals("medium", savedScheme.getRoomId());
        assertEquals("中型展厅", savedScheme.getRoomDisplayName());
        assertEquals("展馆简介", savedScheme.getDescription());
        assertEquals("http://localhost:8080/uploads/scene-cover/cover.jpg", savedScheme.getCoverImageUrl());
        assertEquals(1, savedScheme.getItemCount());
        assertNotNull(savedScheme.getRoomData());
        assertNotNull(savedScheme.getPlacedSnapshot());
        assertEquals("青铜器", savedPlacement.getTitle());
        assertEquals("http://localhost:8080/uploads/models/21/bronze.glb", savedPlacement.getModelUrl());
        assertEquals(88L, response.get("schemeId"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> placements = (List<Map<String, Object>>) response.get("placements");
        assertEquals("http://localhost:8080/uploads/models/21/bronze.glb", placements.get(0).get("modelUrl"));
    }

    @Test
    void createShouldRejectSchemeWhenRoomIdMissing() {
        SceneSchemeService service = new SceneSchemeService(
                mock(SceneSchemeRepository.class),
                mock(ScenePlacementRepository.class),
                mock(ModelRepository.class),
                mock(ModelFileRepository.class),
                mock(ModelFileService.class),
                mock(UserService.class),
                new ObjectMapper()
        );

        SceneSchemeRequest request = new SceneSchemeRequest();

        try {
            service.create(7L, request);
        } catch (IllegalArgumentException ex) {
            assertEquals("roomId 不能为空", ex.getMessage());
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException");
    }

    @Test
    void listPublicShouldReturnSchemeOwnerAndCoverFields() {
        SceneSchemeRepository sceneSchemeRepository = mock(SceneSchemeRepository.class);
        ScenePlacementRepository scenePlacementRepository = mock(ScenePlacementRepository.class);
        ModelRepository modelRepository = mock(ModelRepository.class);
        ModelFileRepository modelFileRepository = mock(ModelFileRepository.class);
        ModelFileService modelFileService = mock(ModelFileService.class);
        UserService userService = mock(UserService.class);

        SceneScheme scheme = new SceneScheme();
        scheme.setSchemeId(5L);
        scheme.setUid(9L);
        scheme.setTitle("青铜展厅");
        scheme.setDescription("方案自己的简介");
        scheme.setCoverImageUrl("http://localhost:8080/uploads/scene-cover/scheme-cover.jpg");
        scheme.setRoomId("medium");
        scheme.setRoomDisplayName("中型展厅");

        ScenePlacement placement = new ScenePlacement();
        placement.setSchemeId(5L);
        placement.setPlacementUid("p-1");
        placement.setMid(21L);
        placement.setTitle("青铜器");
        placement.setModelUrl("http://localhost:8080/static/models/example.glb");

        ModelEntity model = new ModelEntity();
        model.setMid(21L);
        model.setThumbnailUrl("http://localhost:8080/static/thumbs/example.png");
        model.setStory("展馆说明");

        User owner = new User();
        owner.setUid(9L);
        owner.setNickname("策展人");
        owner.setAvatarUrl("http://localhost/avatar.png");

        when(sceneSchemeRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(scheme));
        when(scenePlacementRepository.findBySchemeIdOrderBySortOrderAscCreatedAtAsc(5L)).thenReturn(List.of(placement));
        when(modelRepository.findById(21L)).thenReturn(java.util.Optional.of(model));
        when(userService.getUserMapByIds(List.of(9L))).thenReturn(Map.of(9L, owner));

        SceneSchemeService service = new SceneSchemeService(
                sceneSchemeRepository,
                scenePlacementRepository,
                modelRepository,
                modelFileRepository,
                modelFileService,
                userService,
                new ObjectMapper()
        );

        Map<String, Object> result = service.listPublic("青铜", null).get(0);

        assertEquals("策展人", result.get("nickname"));
        assertEquals("http://localhost/avatar.png", result.get("avatarUrl"));
        assertEquals("http://localhost:8080/static/models/example.glb", result.get("coverModelUrl"));
        assertEquals("http://localhost:8080/uploads/scene-cover/scheme-cover.jpg", result.get("coverImageUrl"));
        assertEquals("方案自己的简介", result.get("description"));
    }
}
