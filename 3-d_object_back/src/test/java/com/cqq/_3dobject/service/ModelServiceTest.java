package com.cqq._3dobject.service;

import com.cqq._3dobject.dto.ModelUploadDTO;
import com.cqq._3dobject.dto.ModelFileUrlDTO;
import com.cqq._3dobject.entity.ModelEntity;
import com.cqq._3dobject.repository.ModelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModelServiceTest {

    @Test
    void saveModelWithTagsShouldPreserveProvidedRenderStatus() {
        ModelRepository modelRepository = mock(ModelRepository.class);
        when(modelRepository.save(any(ModelEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModelService modelService = new ModelService();
        ReflectionTestUtils.setField(modelService, "modelRepository", modelRepository);
        ReflectionTestUtils.setField(modelService, "modelTagService", mock(ModelTagService.class));

        ModelEntity model = new ModelEntity();
        model.setRenderStatus(ModelEntity.RenderStatus.success);

        ModelUploadDTO dto = new ModelUploadDTO();
        dto.setModel(model);

        ModelEntity saved = modelService.saveModelWithTags(dto);

        assertEquals(ModelEntity.RenderStatus.success, saved.getRenderStatus());
    }

    @Test
    void saveModelWithTagsShouldBindProvidedModelFileUrls() {
        ModelRepository modelRepository = mock(ModelRepository.class);
        when(modelRepository.save(any(ModelEntity.class))).thenAnswer(invocation -> {
            ModelEntity model = invocation.getArgument(0);
            model.setMid(76L);
            return model;
        });

        ModelFileService modelFileService = mock(ModelFileService.class);
        ModelService modelService = new ModelService();
        ReflectionTestUtils.setField(modelService, "modelRepository", modelRepository);
        ReflectionTestUtils.setField(modelService, "modelTagService", mock(ModelTagService.class));
        ReflectionTestUtils.setField(modelService, "modelFileService", modelFileService);

        ModelFileUrlDTO file = new ModelFileUrlDTO();
        file.setFileUrl("https://ai.example.com/result/model.glb");
        file.setFileType("GLB");

        ModelUploadDTO dto = new ModelUploadDTO();
        dto.setModel(new ModelEntity());
        dto.setModelFiles(List.of(file));

        modelService.saveModelWithTags(dto);

        verify(modelFileService).bindFileUrl(
                eq(76L),
                eq("https://ai.example.com/result/model.glb"),
                eq("GLB"),
                eq(null),
                eq(null),
                eq(null)
        );
    }
}
