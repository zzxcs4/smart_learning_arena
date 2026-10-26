package com.zzx.exam.service;


import com.zzx.exam.entity.Question;
import com.zzx.exam.vo.AiGenerateRequestVo;
import com.zzx.exam.vo.BatchGenerateVO;
import com.zzx.exam.vo.GradingResult;

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