package com.atguigu.exam.service;

import com.atguigu.exam.entity.AnswerRecord;
import com.atguigu.exam.entity.StudyEffect;
import com.atguigu.exam.vo.StudyEffectVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface StudyEffectService extends IService<StudyEffect> {
    StudyEffectVO getStudyEffect();
    StudyEffectVO getStudyEffect(List<AnswerRecord> list,List<Integer> list2);
    StudyEffectVO getStudyEffectBySid(int Sid);
}
