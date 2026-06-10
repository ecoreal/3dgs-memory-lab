package com.cqq._3dobject.service;

import com.cqq._3dobject.dto.ModelUploadDTO;
import com.cqq._3dobject.dto.ModelFileUrlDTO;
import com.cqq._3dobject.entity.ModelEntity;
import com.cqq._3dobject.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelTagService modelTagService;

    @Autowired
    private ModelFileService modelFileService;

    /**
     * 1. 初始保存（上传阶段）
     */
    @Transactional
    public ModelEntity saveModelWithTags(ModelUploadDTO dto) {
        ModelEntity model = dto.getModel();
        if (model.getRenderStatus() == null) {
            model.setRenderStatus(ModelEntity.RenderStatus.waiting);
        }
        ModelEntity savedModel = modelRepository.save(model);

        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            modelTagService.addTags(savedModel.getMid(), dto.getTags());
        }
        if (dto.getModelFiles() != null && !dto.getModelFiles().isEmpty()) {
            for (ModelFileUrlDTO file : dto.getModelFiles()) {
                modelFileService.bindFileUrl(
                        savedModel.getMid(),
                        file.getFileUrl(),
                        file.getFileType(),
                        file.getOriginalName(),
                        file.getFileSize(),
                        file.getContentType()
                );
            }
        }
        return savedModel;
    }

    /**
     * 2. 更新模型详情（编辑页阶段）
     * 修复 ClassCastException 并添加 categoryId 处理
     */
    @Transactional
    public ModelEntity updateModelDetails(Long mid, Map<String, Object> updateMap, Long currentUid) {
        // 1. 查找现有模型
        ModelEntity existingModel = modelRepository.findById(mid)
                .orElseThrow(() -> new RuntimeException("未找到 ID 为 " + mid + " 的模型"));

        // 2. 安全校验
        if (!existingModel.getUid().equals(currentUid)) {
            throw new RuntimeException("权限不足：您不是该模型的所有者");
        }

        // 3. 字段映射更新 (使用 .toString() 配合类型转换更安全)
        if (updateMap.get("title") != null) {
            existingModel.setTitle(updateMap.get("title").toString());
        } else if (updateMap.get("name") != null) {
            existingModel.setTitle(updateMap.get("name").toString());
        }

        if (updateMap.get("story") != null) {
            existingModel.setStory(updateMap.get("story").toString());
        }

        if (updateMap.get("productionYear") != null) {
            existingModel.setProductionYear(Integer.valueOf(updateMap.get("productionYear").toString()));
        } else if (updateMap.get("year") != null) {
            existingModel.setProductionYear(Integer.valueOf(updateMap.get("year").toString()));
        }

        // ✨ 新增：处理 categoryId
        if (updateMap.get("categoryId") != null) {
            existingModel.setCategoryId(Integer.valueOf(updateMap.get("categoryId").toString()));
        }

        if (updateMap.get("isPublic") != null) {
            existingModel.setIsPublic(Boolean.valueOf(updateMap.get("isPublic").toString()));
        }

        // 4. 更新标签 - 彻底修复 ClassCastException
        if (updateMap.get("tags") != null) {
            Object tagsObj = updateMap.get("tags");
            List<String> newTags;

            if (tagsObj instanceof String) {
                // 如果前端传的是 "tag1,tag2" 字符串，则分割转为 List
                String tagsStr = (String) tagsObj;
                newTags = Arrays.stream(tagsStr.split(","))
                        .filter(s -> !s.trim().isEmpty())
                        .collect(Collectors.toList());
            } else if (tagsObj instanceof List) {
                // 如果前端传的是数组，则强转
                newTags = (List<String>) tagsObj;
            } else {
                newTags = null;
            }

            if (newTags != null) {
                modelTagService.deleteTagsByMid(mid);
                modelTagService.addTags(mid, newTags);
            }
        }

        return modelRepository.save(existingModel);
    }

    /**
     * 3. 获取单个详情
     */
    public ModelEntity getModelById(Long mid) {
        return modelRepository.findById(mid).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ModelEntity> getPublicModels() {
        return modelRepository.findByIsPublicTrue();
    }

    @Transactional(readOnly = true)
    public List<ModelEntity> searchPublicModels(String keyword, Integer categoryId) {
        return modelRepository.searchPublic(keyword, categoryId);
    }

    @Transactional(readOnly = true)
    public List<ModelEntity> getUserModels(Long uid) {
        List<ModelEntity> models = modelRepository.findByUid(uid);
        if (models.isEmpty()) {
            return models;
        }

        Map<Long, List<String>> formatsByModelId = modelFileService.getDistinctFormatsByModelIds(
                models.stream().map(ModelEntity::getMid).toList()
        );

        models.forEach(model -> model.setFormats(
                new ArrayList<>(formatsByModelId.getOrDefault(model.getMid(), List.of()))
        ));
        return models;
    }
}
