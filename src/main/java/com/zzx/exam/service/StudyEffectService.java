package com.zzx.exam.service;

import com.zzx.exam.entity.AnswerRecord;
import com.zzx.exam.entity.StudyEffect;
import com.zzx.exam.vo.StudyEffectVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface StudyEffectService extends IService<StudyEffect> {
    StudyEffectVO getStudyEffect();
    StudyEffectVO getStudyEffect(List<AnswerRecord> list,List<Integer> list2);
    StudyEffectVO getStudyEffectBySid(int Sid);
}
