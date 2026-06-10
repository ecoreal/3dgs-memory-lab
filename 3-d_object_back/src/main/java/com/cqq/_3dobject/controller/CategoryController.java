package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.Category;
import com.cqq._3dobject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 接口：GET http://localhost:8080/api/categories
    // 作用：前端获取全部分类标签
    @GetMapping
    public List<Category> list() {
        return categoryService.getAllCategories();
    }

    // 接口：POST http://localhost:8080/api/categories
    // 参数示例：{"name": "机械零件"}
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.addCategory(category.getName());
    }
}