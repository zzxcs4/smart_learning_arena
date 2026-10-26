package com.zzx.exam.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * @TableName study_effect
 */
@TableName(value ="study_effect")
@Data
public class StudyEffect {
    /**
     *
     */
    @TableId
    private Integer id;

    /**
     *
     */
    private String studyEffect;

    /**
     *
     */
    private String studyEffectType;

    /**
     *
     */
    private Date statDate;
}