package com.atguigu.exam.vo;

import com.atguigu.exam.entity.Class;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class CourseVO {
    /**
     * 课程id
     */
    private Long id;
    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程教师
     */
    private String teacherName;

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

    /**
     * 课程下班级
     */
    private List<Class> Classes;

    /**
     * 课程创建用户
     */
    private String userUrl;
}
