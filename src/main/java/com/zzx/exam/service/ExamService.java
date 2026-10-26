package com.zzx.exam.service;

import com.zzx.exam.entity.ExamRecord;
import com.zzx.exam.vo.ExamRankingVO;
import com.zzx.exam.vo.StartExamVo;
import com.zzx.exam.vo.SubmitAnswerVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 考试服务接口
 */
public interface ExamService extends IService<ExamRecord> {

    ExamRecord startExam(StartExamVo startExamVo);

    ExamRecord customGetExamRecordById(Integer id);

    void customSubmitAnswer(Integer examRecordId, List<SubmitAnswerVo> answers) throws InterruptedException;

    ExamRecord gradeExam(Integer examRecordId) throws InterruptedException;

    void customRemoveById(Integer id);

    List<ExamRankingVO> customGetRanking(Integer paperId, Integer limit, Long courseId, Long classID);
} 
 