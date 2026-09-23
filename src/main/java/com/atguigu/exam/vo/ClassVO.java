package com.atguigu.exam.vo;

import lombok.Data;

@Data
public class ClassVO {
    private Long id;

    /**
     * 班级名称（如：计算机科学与技术2023级1班）
     */
    private String className;

    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程教师
     */
    private String teacherName;
    /**
     * 课程创建用户
     */
    private String userUrl;

    /**
     * 课程简介
     */
    private String courseIntro;

    /**
     * 开课学期（如：2024-2025学年第一学期）
     */
    private String semester;

    /**
     * 课程封面图片URL
     */
    private String courseCover;


}
