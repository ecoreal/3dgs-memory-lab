package com.cqq._3dobject.dto;

import com.cqq._3dobject.entity.ModelEntity;
import java.util.List;

/**
 * 模型上传组合包
 * 包含了模型的基本信息和自定义标签列表
 */
public class ModelUploadDTO {

    private ModelEntity model;    // 对应 models 表
    private List<String> tags;    // 对应自定义标签字符串列表
    private List<ModelFileUrlDTO> modelFiles; // 已生成的模型文件 URL

    public ModelUploadDTO() {}

    // --- Getter & Setter ---
    public ModelEntity getModel() {
        return model;
    }

    public void setModel(ModelEntity model) {
        this.model = model;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ModelFileUrlDTO> getModelFiles() {
        return modelFiles;
    }

    public void setModelFiles(List<ModelFileUrlDTO> modelFiles) {
        this.modelFiles = modelFiles;
    }
}
