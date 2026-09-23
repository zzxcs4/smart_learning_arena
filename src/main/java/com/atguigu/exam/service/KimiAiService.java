package com.atguigu.exam.service;


import com.atguigu.exam.entity.Question;
import com.atguigu.exam.vo.AiGenerateRequestVo;
import com.atguigu.exam.vo.BatchGenerateVO;
import com.atguigu.exam.vo.GradingResult;
import com.atguigu.exam.vo.QuestionImportVo;

import java.util.List;

/**
 * Kimi AI服务接口
 * 用于调用Kimi API生成题目
 */
public interface KimiAiService {
    String buildPrompt(AiGenerateRequestVo request);

    GradingResult gradingTextQuestion(Question question, String userAnswer, Integer maxScore) throws InterruptedException;

    String buildSummary(Integer totalScore, Integer maxScore, Integer questionCount, Integer correctCount) throws InterruptedException;

    BatchGenerateVO aiGenerateQuestions(AiGenerateRequestVo request) throws InterruptedException;

    String callKimiAi(String prompt) throws InterruptedException;
} 