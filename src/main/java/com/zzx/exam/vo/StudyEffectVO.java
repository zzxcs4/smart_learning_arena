package com.zzx.exam.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StudyEffectVO {
    //平均正确率趋势
    List<Float> AvgAccuracyTrend;
    //知识点掌握情况
    String Mastery;
    //高频错误知识点
    List<Map.Entry<String, Integer>> highErrorPoints;
}