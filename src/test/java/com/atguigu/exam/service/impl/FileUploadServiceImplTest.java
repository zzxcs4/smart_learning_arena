package com.atguigu.exam.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FileUploadServiceImplTest {
    @Autowired
    private FileUploadServiceImpl fileUploadService;
    @Test
    void uploadFileByContent() {
        String string = fileUploadService.uploadFileByContent("（一）教学目标：  \n" +
                "1. 了解Web的基本概念及其核心特征；  \n" +
                "2. 掌握Web应用的定义、组成及运行方式；  \n" +
                "3. 能区分静态Web资源与动态Web资源；  \n" +
                "4. 初步认识Web应用涉及的主要文件类型及用户使用流程。", 3L);
        System.out.println(string);
    }
}