package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.ExportTask;
import com.cqq._3dobject.service.ExportTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exports")
public class ExportTaskController {

    @Autowired
    private ExportTaskService exportTaskService;

    // 接口：提交导出请求
    @PostMapping("/submit")
    public ExportTask submit(@RequestBody ExportTask task) {
        return exportTaskService.createExportTask(task);
    }

    // 接口：查看我的导出历史
    @GetMapping("/history/{uid}")
    public List<ExportTask> getHistory(@PathVariable("uid") Long uid) {
        return exportTaskService.getUserTasks(uid);
    }
}
