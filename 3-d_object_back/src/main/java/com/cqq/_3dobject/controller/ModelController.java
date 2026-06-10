package com.cqq._3dobject.controller;

import com.cqq._3dobject.dto.ModelUploadDTO;
import com.cqq._3dobject.entity.ModelEntity;
import com.cqq._3dobject.service.ModelService;
import com.cqq._3dobject.service.ViewAssembler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @Autowired
    private ViewAssembler viewAssembler;

    /**
     * 接口 1: 创建建模任务（上传/存入后端）
     * 此时主要保存图片 URL，生成初始记录并返回 ID
     */
    @PostMapping("/upload")
    public ModelEntity uploadModel(@RequestBody ModelUploadDTO uploadDto, HttpServletRequest request) {
        Long currentUid = (Long) request.getAttribute("current_uid");

        if (uploadDto.getModel() != null) {
            uploadDto.getModel().setUid(currentUid);
        }

        // 保存后，JPA/MyBatis 会将生成的自增 ID 填充回实体类
        return modelService.saveModelWithTags(uploadDto);
    }

    /**
     * 接口 2: 更新模型详情
     * 将 ModelEntity 改为 Map<String, Object> 以匹配 Service 层
     */
    @PostMapping("/update/{id}")
    public ModelEntity updateModel(@PathVariable("id") Long id,
                                   @RequestBody java.util.Map<String, Object> updateData, // 💡 修改这里
                                   HttpServletRequest request) {
        // 获取当前用户 UID
        Long currentUid = (Long) request.getAttribute("current_uid");

        // 调用 Service 层，此时参数类型就匹配了
        return modelService.updateModelDetails(id, updateData, currentUid);
    }

    /**
     * 接口 3: 获取单个模型详情 (建议增加)
     * 用于 editor 页面加载时显示已有的图片和初始信息
     */
    @GetMapping("/detail/{id}")
    public ModelEntity getModelDetail(@PathVariable("id") Long id) {
        return modelService.getModelById(id);
    }

    /**
     * 接口 4: 获取公开展示的模型 (首页使用)
     */
    @GetMapping("/public")
    public List<Map<String, Object>> listPublic(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        return viewAssembler.toPublicModelViews(modelService.searchPublicModels(keyword, categoryId));
    }

    /**
     * 接口 5: 获取“我”的模型列表
     */
    @GetMapping("/my")
    public List<ModelEntity> listMyModels(HttpServletRequest request) {
        Long currentUid = (Long) request.getAttribute("current_uid");
        return modelService.getUserModels(currentUid);
    }
}
