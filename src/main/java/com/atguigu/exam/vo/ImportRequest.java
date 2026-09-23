package com.atguigu.exam.vo;

import lombok.Data;

@Data
public class ImportRequest {
    private String teachingContent;
    private Long courseId;
}