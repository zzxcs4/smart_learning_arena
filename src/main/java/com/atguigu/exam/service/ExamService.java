package com.atguigu.exam.service;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.vo.ExamRankingVO;
import com.atguigu.exam.vo.StartExamVo;
import com.atguigu.exam.vo.SubmitAnswerVo;
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

    List<ExamRankingVO> customGetRanking(Integer paperId, Integer limit,Long courseId,Long classID);
} 
 