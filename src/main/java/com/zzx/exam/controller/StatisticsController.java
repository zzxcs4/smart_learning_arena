package com.zzx.exam.controller;

import cn.hutool.core.util.ObjectUtil;
import com.zzx.exam.common.Result;
import com.zzx.exam.entity.AnswerRecord;
import com.zzx.exam.entity.ClassCourseRelation;
import com.zzx.exam.entity.ExamRecord;
import com.zzx.exam.entity.Paper;
import com.atguigu.exam.service.*;
import com.zzx.exam.vo.StudyEffectVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzx.exam.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DS("mysql")
@RestController
@RequestMapping("/api/statistic")
@Tag(name = "数据统计",description = "各种数据统计")
public class StatisticsController {
    @Autowired
    private StudyEffectService studyEffectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamRecordService examRecordService;
    @Autowired
    private AnswerRecordService answerRecordService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private ClassCourseRelationService classCourseRelationService;
    /**
     * 获取学生学习效果接口
     *
     * @param
     * @return
     */
    @Operation(
            summary = "获取学生学习效果",
            description = "获取学生学习效果接口"
    )
    @GetMapping("/studyEffect")
    public Result<StudyEffectVO> getDashboardStats4(@RequestParam(value = "courseId",required = false) Long courseId, @RequestParam(value = "classId",required = false) Long classId, HttpServletRequest request){
        List<Long> examIdList=new ArrayList<>();
        if(ObjectUtil.isNotEmpty(classId)){
            QueryWrapper<ClassCourseRelation> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("class_id",classId);
            queryWrapper3.eq(ObjectUtil.isNotEmpty(courseId),"course_id",courseId);
            Long CCId = classCourseRelationService.getOne(queryWrapper3).getId();
            QueryWrapper<Paper> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq(ObjectUtil.isNotNull(CCId),"class_course_id", CCId);
            examIdList = paperService.list(queryWrapper2).stream().map(Paper::getId).collect(Collectors.toList());
        }

        String username = userService.getLoginUser(request).getRealName();
        System.out.println("学生名字："+username);
        QueryWrapper<ExamRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(ObjectUtil.isNotNull(examIdList),"exam_id", examIdList);
        queryWrapper.eq(StringUtil.isNotBlank(username),"student_name", username);

        List<Integer> examRecordIds = examRecordService.list(queryWrapper).stream().map(ExamRecord::getId).collect(Collectors.toList());
        System.out.println("考试记录ids："+examRecordIds);
        QueryWrapper<AnswerRecord> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.in(ObjectUtil.isNotEmpty(examRecordIds),"exam_record_id", examRecordIds);
        //.stream().map(answerRecord -> Long.valueOf(answerRecord.getQuestionId().toString())).collect(Collectors.toList());

        List<AnswerRecord> list = answerRecordService.list(queryWrapper1);
        return Result.success(studyEffectService.getStudyEffect(list,examRecordIds));
    }

}
