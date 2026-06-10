package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.ModelTag;
import com.cqq._3dobject.repository.ModelTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelTagService {

    @Autowired
    private ModelTagRepository repository;

    /**
     * 批量保存标签
     */
    @Transactional
    public List<ModelTag> addTags(Long mid, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return null;

        List<ModelTag> tags = tagNames.stream()
                .map(name -> new ModelTag(mid, name))
                .collect(Collectors.toList());
        return repository.saveAll(tags);
    }

    /**
     * 💡 补充：根据模型 ID 删除所有标签
     * 用于编辑时的“覆盖更新”逻辑
     */
    @Transactional
    public void deleteTagsByMid(Long mid) {
        // 调用 Repository 的删除方法
        repository.deleteByMid(mid);
    }

    /**
     * 获取模型的所有标签
     */
    public List<ModelTag> getTagsByMid(Long mid) {
        return repository.findByMid(mid);
    }
}