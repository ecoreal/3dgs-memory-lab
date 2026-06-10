package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // JpaRepository 自带了 findAll, save, deleteById 等所有基础功能
}