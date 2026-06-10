package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.ExportTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExportTaskRepository extends JpaRepository<ExportTask, Long> {
    // 查找某个用户的所有导出历史
    List<ExportTask> findByUidOrderByCreatedAtDesc(Long uid);
}