package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ModelRepository extends JpaRepository<ModelEntity, Long> {
    // 自动按用户 ID 查询
    List<ModelEntity> findByUid(Long uid);

    // 自动按公开状态查询（用于首页展示）
    List<ModelEntity> findByIsPublicTrue();

    // 模糊搜索公开模型
    List<ModelEntity> findByTitleContainingAndIsPublicTrue(String keyword);

    /**
     * 综合搜索：支持关键字（标题或标签）和分类筛选
     */
    @Query("SELECT DISTINCT m FROM ModelEntity m " +
            "WHERE m.isPublic = true " +
            "AND (:categoryId IS NULL OR m.categoryId = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR m.mid IN (SELECT t.mid FROM ModelTag t WHERE LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    List<ModelEntity> searchPublic(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId);
}
