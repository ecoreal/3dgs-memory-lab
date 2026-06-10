package com.cqq._3dobject.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name; // 分类名称：如“家具”、“工业”、“艺术品”

    // 1. 显式编写无参构造函数（JPA 必须要求）
    public Category() {
    }

    // 2. 有参构造函数（方便代码中快速创建对象）
    public Category(String name) {
        this.name = name;
    }

    // 3. 手写 Getter 和 Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 4. 重写 toString（方便在控制台打印调试）
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}