package com.zzx.exam.service;

import com.zzx.exam.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {


    List<Category> getAllCategories();

    List<Category> getCategoryTree(Long ccId,Long courseId);

    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long id);
} 