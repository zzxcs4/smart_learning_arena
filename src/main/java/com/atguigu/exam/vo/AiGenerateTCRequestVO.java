package com.atguigu.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiGenerateTCRequestVO {
    private Long courseId;
    private Long classId;

    private String topic; // 生成题目的主题，如"Spring框架"

    private Integer template;
    private String other;
}
