package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.Category;
import com.cqq._3dobject.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest {

    @Test
    void listShouldReturnCategoriesFromService() throws Exception {
        CategoryService categoryService = mock(CategoryService.class);
        Category category = new Category();
        category.setId(3);
        category.setName("文物");
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        MockMvc mockMvc = buildMockMvc(categoryService);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("文物"));
    }

    @Test
    void createShouldUseCategoryNameFromBody() throws Exception {
        CategoryService categoryService = mock(CategoryService.class);
        Category created = new Category();
        created.setId(9);
        created.setName("机械零件");
        when(categoryService.addCategory("机械零件")).thenReturn(created);

        MockMvc mockMvc = buildMockMvc(categoryService);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "机械零件"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.name").value("机械零件"));

        verify(categoryService).addCategory("机械零件");
    }

    private static MockMvc buildMockMvc(CategoryService categoryService) {
        CategoryController controller = new CategoryController();
        ReflectionTestUtils.setField(controller, "categoryService", categoryService);
        return MockMvcBuilders.standaloneSetup(controller).build();
    }
}
