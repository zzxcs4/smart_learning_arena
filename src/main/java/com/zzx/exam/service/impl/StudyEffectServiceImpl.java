package com.zzx.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.zzx.exam.entity.AnswerRecord;
import com.zzx.exam.entity.Question;
import com.zzx.exam.entity.StudyEffect;
import com.zzx.exam.mapper.StudyEffectMapper;
import com.zzx.exam.service.AnswerRecordService;
import com.zzx.exam.service.CategoryService;
import com.zzx.exam.service.QuestionService;
import com.zzx.exam.service.StudyEffectService;
import com.zzx.exam.vo.StudyEffectVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DS("mysql")
@Service
public class StudyEffectServiceImpl extends ServiceImpl<StudyEffectMapper, StudyEffect> implements StudyEffectService {
    @Resource
    private QuestionService questionService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private AnswerRecordService answerRecordService;
    @Override
    public StudyEffectVO getStudyEffect() {
        return null;
    }

    @Override
    public StudyEffectVO getStudyEffect(List<AnswerRecord> list,List<Integer> examRecordIds) {
        StudyEffectVO studyEffectVO=new StudyEffectVO();
        List<Long> rids = new ArrayList<>();
        List<Long> cqids = new ArrayList<>();

        for(AnswerRecord answerRecord : list){
            if(answerRecord.getScore()==0){
                rids.add(Long.valueOf(answerRecord.getQuestionId().toString()));

            }else {
                cqids.add( Long.valueOf(answerRecord.getQuestionId().toString()));
            }
        }
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.in(ObjectUtil.isNotEmpty(rids),"id",rids);
//        List<String> masteredPoints = questionService.list(wrapper).stream().map(question->
//                categoryService.getById(question.getCategoryId()).getName())
//                .collect(Collectors.toList());
        QueryWrapper<Question> wrapper1 = new QueryWrapper<>();
        wrapper1.in(ObjectUtil.isNotEmpty(cqids),"id",cqids);
        List<String> notMasteredPoints=questionService.list(wrapper1).stream().map(question->
                         categoryService.getById(question.getCategoryId()).getName())
                 .collect(Collectors.toList());

        // 单独构建输出字符串得知识点掌握情况
        StringBuilder output = new StringBuilder();
//        output.append("=== 已掌握知识点 ===\n");
//        masteredPoints.forEach(point -> output.append(point).append("\n"));
        output.append("\n=== 未掌握知识点 ===\n");
        notMasteredPoints.forEach(point -> output.append(point).append("\n"));
        studyEffectVO.setMastery(output.toString());
        // 按知识点出现频率排序得高频错误知识点
        // 用于统计的HashMap
        Map<String, Integer> notMasteredCountMap = new HashMap<>();
        for (String point : notMasteredPoints) {
            //notMasteredPoints.add(point);
            notMasteredCountMap.put(point, notMasteredCountMap.getOrDefault(point, 0) + 1);
        }
        List<Map.Entry<String, Integer>> notMasteredSorted = new ArrayList<>(notMasteredCountMap.entrySet());
        notMasteredSorted.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // 降序排序
        studyEffectVO.setHighErrorPoints(notMasteredSorted);
        // 计算正确率趋势
        List<Float> accuracyList = new ArrayList<>();
        for (Integer examRecordId :examRecordIds){
            QueryWrapper<AnswerRecord> wrapper2 = new QueryWrapper<>();
            wrapper2.in(ObjectUtil.isNotEmpty(examRecordId),"exam_record_id", examRecordId);
            long count = answerRecordService.count(wrapper2);
            QueryWrapper<AnswerRecord> wrapper3 = new QueryWrapper<>();
            wrapper3.in(ObjectUtil.isNotEmpty(examRecordId),"exam_record_id", examRecordId);
            wrapper3.eq("score", 0);
            long count1 = answerRecordService.count(wrapper3);
            accuracyList.add((float)(count-count1)/ count);
        }
        studyEffectVO.setAvgAccuracyTrend(accuracyList);
        return studyEffectVO;
    }

    @Override
    public StudyEffectVO getStudyEffectBySid(int Sid) {
        return null;
    }
}
