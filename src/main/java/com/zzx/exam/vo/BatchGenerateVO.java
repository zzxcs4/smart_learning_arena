package com.zzx.exam.vo;

import lombok.Data;

import java.util.List;
@Data
public class BatchGenerateVO {
    private List<QuestionImportVo>  questionImportVoList;
    private Long ccId;
}
