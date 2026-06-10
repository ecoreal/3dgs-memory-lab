package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.Category;
import com.cqq._3dobject.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 获取所有分类（用于前端首页的分类导航栏）
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 添加新分类
    public Category addCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }
}