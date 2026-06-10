package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.ModelTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelTagRepository extends JpaRepository<ModelTag, Long> {

    // 根据模型 ID 查找该模型下的所有标签
    List<ModelTag> findByMid(Long mid);

    // 删除某个模型下的特定标签名
    void deleteByMidAndTagName(Long mid, String tagName);
    void deleteByMid(Long mid);
}