package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.ModelTag;
import com.cqq._3dobject.service.ModelTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model-tags")
public class ModelTagController {

    @Autowired
    private ModelTagService modelTagService;

    // 接口：批量为模型绑定标签
    @PostMapping("/batch")
    public List<ModelTag> batchAddTags(@RequestParam("mid") Long mid, @RequestBody List<String> tagNames) {
        return modelTagService.addTags(mid, tagNames);
    }

    // 接口：获取某个模型关联的所有标签
    @GetMapping("/model/{mid}")
    public List<ModelTag> getTags(@PathVariable("mid") Long mid) {
        return modelTagService.getTagsByMid(mid);
    }
}
