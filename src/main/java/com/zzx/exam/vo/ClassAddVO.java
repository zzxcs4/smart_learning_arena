package com.zzx.exam.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ClassAddVO {
    /**
     * 班级id
     */
    private Long id;

    /**
     * 班级名称（如：计算机科学与技术2023级1班）
     */
    private String className;

    /**
     * 课程id
     */
    private Long courseId;
}
