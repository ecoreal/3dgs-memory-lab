package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 获取某个模型下的所有评论，并按时间倒序排列
    List<Comment> findByMidOrderByCreatedAtDesc(Long mid);

    // 统计某个模型的评论数
    long countByMid(Long mid);
}