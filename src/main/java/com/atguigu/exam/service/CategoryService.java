package com.atguigu.exam.service;

import com.atguigu.exam.entity.Banner;
import com.atguigu.exam.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {


    List<Category> getAllCategories();

    List<Category> getCategoryTree(Long ccId,Long courseId);

    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long id);
} 