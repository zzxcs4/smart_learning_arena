package com.atguigu.exam.vo;

import lombok.Data;

import java.util.Date;
@Data
public class CourseQueryRequestVO {
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
     * 创建时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
