package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.ExportTask;
import com.cqq._3dobject.repository.ExportTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExportTaskService {

    @Autowired
    private ExportTaskRepository repository;

    // 创建导出任务
    public ExportTask createExportTask(ExportTask task) {
        // 默认状态设为待处理
        task.setStatus(ExportTask.TaskStatus.pending);
        return repository.save(task);
    }

    // 获取用户的导出记录
    public List<ExportTask> getUserTasks(Long uid) {
        return repository.findByUidOrderByCreatedAtDesc(uid);
    }
}