package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.Category;
import com.cqq._3dobject.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    @Test
    void getAllCategoriesShouldReturnRepositoryResult() {
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        Category category = new Category();
        category.setName("文物");
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        CategoryService service = new CategoryService();
        ReflectionTestUtils.setField(service, "categoryRepository", categoryRepository);

        List<Category> categories = service.getAllCategories();

        assertEquals(1, categories.size());
        assertEquals("文物", categories.get(0).getName());
    }

    @Test
    void addCategoryShouldCreateEntityWithProvidedName() {
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService service = new CategoryService();
        ReflectionTestUtils.setField(service, "categoryRepository", categoryRepository);

        Category created = service.addCategory("机械零件");

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        assertEquals("机械零件", categoryCaptor.getValue().getName());
        assertEquals("机械零件", created.getName());
    }
}
