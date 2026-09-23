package com.atguigu.exam.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.atguigu.exam.entity.*;
import com.atguigu.exam.entity.Class;
import com.atguigu.exam.mapper.*;
import com.atguigu.exam.service.*;
import com.atguigu.exam.vo.AiPaperVo;
import com.atguigu.exam.vo.PaperVo;
import com.atguigu.exam.vo.RuleVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 试卷服务实现类
 */
@DS("mysql")
@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private PaperQuestionService paperQuestionService;

    @Autowired
    private ExamRecordMapper examRecordMapper;
@Autowired
private ClassCourseRelationService classCourseRelationService;
    @Autowired
    private QuestionAnswerMapper questionAnswerMapper;
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private QuestionChoiceMapper questionChoiceMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    /**
     * 根据试卷id试卷详情
     * 试卷对象
     * 题目集合
     * 注意： 题目的选项sort正序
     * 注意： 所有题目根据类型排序
     * @param id 试卷id
     * @return
     */
    @Override
    public Paper customPaperDetailById(Long id) {
        // 1. 查询试卷基本信息
        Paper paper = baseMapper.selectById(id); // 查询试卷
        if (paper == null) {
            return null; // 如果试卷不存在，返回null
        }

        // 2. 查询试卷包含的所有题目ID和分值
        QueryWrapper<PaperQuestion> pqWrapper = new QueryWrapper<>();
        pqWrapper.eq("paper_id", id); // 设置查询条件
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(pqWrapper); // 查询关联记录
        if (paperQuestions.isEmpty()) {
            paper.setQuestions(new ArrayList<>());
            return paper;
        }

        List<Long> questionIds = paperQuestions.stream().map(PaperQuestion::getQuestionId).collect(Collectors.toList());

        // 3. 根据题目ID查询题目详情
        List<Question> questions = questionMapper.selectBatchIds(questionIds);

        // 4. 优化：批量查询避免N+1问题
        // 4.1 填充分值信息
        Map<Long, BigDecimal> scoreMap = paperQuestions.stream()
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getScore));

        // 批量查询选项
        List<QuestionChoice> allChoices = questionChoiceMapper.selectList(
                new QueryWrapper<QuestionChoice>().in("question_id", questionIds)
        );
        Map<Long, List<QuestionChoice>> choicesMap = allChoices.stream()
                .collect(Collectors.groupingBy(QuestionChoice::getQuestionId));

        // 批量查询答案
        List<QuestionAnswer> allAnswers = questionAnswerMapper.selectList(
                new QueryWrapper<QuestionAnswer>().in("question_id", questionIds)
        );
        Map<Long, QuestionAnswer> answersMap = allAnswers.stream()
                .collect(Collectors.toMap(QuestionAnswer::getQuestionId, answer -> answer));

        // 4.3 为每个题目设置信息
        questions.forEach(q -> {
            // 设置分值
            q.setPaperScore(scoreMap.get(q.getId()));

            // 设置选项（仅选择题）
            if ("CHOICE".equals(q.getType())) {
                List<QuestionChoice> choices = choicesMap.getOrDefault(q.getId(), new ArrayList<>());
                // 按sort字段排序
                choices.sort((c1, c2) -> Integer.compare(
                        c1.getSort() != null ? c1.getSort() : 0,
                        c2.getSort() != null ? c2.getSort() : 0
                ));
                q.setChoices(choices);
            }

            // 设置答案
            q.setAnswer(answersMap.get(q.getId()));
        });

        // 5. 按题目类型排序：选择题 -> 判断题 -> 简答题
        questions.sort((q1, q2) -> {
            int order1 = getTypeOrder(q1.getType());
            int order2 = getTypeOrder(q2.getType());
            return Integer.compare(order1, order2);
        });

        paper.setQuestions(questions); // 设置题目列表

        return paper; // 返回试卷详情
    }
    /**
     * 获取题目类型的排序顺序
     * @param type 题目类型
     * @return 排序序号
     */
    private int getTypeOrder(String type) {
        switch (type) {
            case "CHOICE": return 1; // 选择题
            case "JUDGE": return 2;  // 判断题
            case "TEXT": return 3;   // 简答题
            default: return 4;       // 其他类型
        }
    }
    @Override
    public Paper customCreatePaper(PaperVo paperVo) {
        //1. 完善试卷内信息 名字 描述 时间  -> 状态 ，总题目数 ， 总分数
        Paper paper = new Paper();
        //名字 描述 时间
        BeanUtils.copyProperties(paperVo,paper);
        //态 ，总题目数, 总分数
        paper.setStatus("DRAFT");
        if (ObjectUtils.isEmpty(paperVo.getQuestions())){
            //本次没选题目
            paper.setTotalScore(BigDecimal.ZERO);
            paper.setQuestionCount(0);
            save(paper);
            log.warn("本次{}组卷，没有选择题目！注意没有题目的试卷无法进行考试！！",paper);
            return paper;
        }
    /*
        状态默认值： DRAFT
        总题目数： question长度
        总分数： question分数的和
     */
        paper.setQuestionCount(paperVo.getQuestions().size());
        paper.setTotalScore(paperVo.getQuestions().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        //2. 完成试卷的插入 -》 主键回显 paperId
        QueryWrapper<ClassCourseRelation> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("class_id", paperVo.getClassId());
        queryWrapper1.eq("course_id",paperVo.getCourseId());
        Long ccId = classCourseRelationService.getOne(queryWrapper1).getId();
        paper.setClassCourseId(ccId);
        save(paper);

        //3. 中间表集合插入 【批量插入】 -》 中间表的service对象
        List<PaperQuestion> paperQuestionList = paperVo.getQuestions().entrySet().stream().
                map(entry -> new PaperQuestion(paper.getId().intValue(), Long.valueOf(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        //4. 中间表的批量插入
        paperQuestionService.saveBatch(paperQuestionList);
        return paper;
    }
    @Override
    public Paper customUpdatePaper(Integer id, PaperVo paperVo) {

        //1.校验 （不能发布状态 ， 不能不同id,name相同）
        Paper paper = getById(id);
        if ("PUBLISHED".equals(paper.getStatus())){
            throw new RuntimeException("发布状态下的试卷不允许修改！");
        }
        //校验id name
        LambdaQueryWrapper<Paper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Paper::getId, id);
        queryWrapper.eq(Paper::getName,paperVo.getName());
        long count = count(queryWrapper);
        if (count > 0){
            throw new RuntimeException("%s 试卷名字已经存在，请重新修改！".formatted(paperVo.getName()));
        }
        //2.试卷的主体
        BeanUtils.copyProperties(paperVo,paper);
    /*
      状态默认值： DRAFT
      总题目数： question长度
      总分数： question分数的和
     */
        paper.setQuestionCount(paperVo.getQuestions().size());
        paper.setTotalScore(paperVo.getQuestions().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        updateById(paper);

        //3. 中间表的批量插入
        paperQuestionService.remove(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId,paper.getId()));
        List<PaperQuestion> paperQuestionList = paperVo.getQuestions().entrySet().stream().
                map(entry -> new PaperQuestion(paper.getId().intValue(), Long.valueOf(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        //4. 中间表的批量插入
        paperQuestionService.saveBatch(paperQuestionList);
        return paper;
    }
    @Override
    public void customRemoveId(Integer id) {
        //1.不是发布状态
        Paper paper = getById(id);
        if (paper == null || "PUBLISHED".equals(paper.getStatus())){
            throw new RuntimeException("发布状态的试卷不能删除！");
        }
        //2.不能有关联的考试记录
        LambdaQueryWrapper<ExamRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamRecord::getExamId,id);
        Long count = examRecordMapper.selectCount(lambdaQueryWrapper);
        if (count > 0){
            throw new RuntimeException("当前试卷：%s 下面有关联 %s条考试记录！无法直接删除！".formatted(id,count));
        }
        //3.删除自身表
        removeById(Long.valueOf(id));
        //4.删除中间表
        paperQuestionService.remove(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId,id));
    }

    @Override
    public void customUpdatePaperStatus(Integer id, String status) {
        Paper paper = getById(id);
        paper.setStatus(status);
        updateById(paper);
    }
    @Override
    public Paper customAiCreatePaper(AiPaperVo aiPaperVo) {
        //1. 试卷的基本属性赋值并保存 （名字 描述 时间 状态）
        Paper paper = new Paper();
        BeanUtils.copyProperties(aiPaperVo,paper);
        paper.setStatus("DRAFT");
        QueryWrapper<ClassCourseRelation> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("class_id", aiPaperVo.getClassId());
        Long ccId = classCourseRelationService.getOne(queryWrapper1).getId();
        paper.setClassCourseId(ccId);
        save(paper);

        //2. 组卷规则下的试题选择和中间表的保存
        int questionCount = 0;
        BigDecimal totalScore = BigDecimal.ZERO;
        for (RuleVo rule : aiPaperVo.getRules()) {
            //步骤1：校验规则下的题目数量 = 0 跳过
            if (rule.getCount() == 0){
                log.warn("在：{}类型下，不需要出题！",rule.getType().name());
                continue;
            }
            //步骤2：查询当前规则下的所有题目集合 【type categoryIds】
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Question::getType, rule.getType().name());
            queryWrapper.in(!ObjectUtils.isEmpty(rule.getCategoryIds()),Question::getCategoryId, rule.getCategoryIds());
            List<Question> allQuestionList = questionMapper.selectList(queryWrapper);

            //步骤3：校验查询的题目集合，集合为空！跳过本次！
            if (ObjectUtils.isEmpty(allQuestionList)){
                log.warn("在：{}类型下，我们指定的分类：{},没有查询到题目信息！",rule.getType().name(),rule.getCategoryIds());
                continue;
            }

            //步骤4：判断下是否有规则下count数量！ 没有要全部了
            int realNumbers = Math.min(rule.getCount(), allQuestionList.size());

            //步骤5：本次规则下添加的数量和分数累加
            questionCount += realNumbers;
            totalScore =  totalScore.add(BigDecimal.valueOf((long) realNumbers * rule.getScore()));

            //步骤6：先打乱数据，再截取需要题目数量
            Collections.shuffle(allQuestionList);
            List<Question> realQuestionList = allQuestionList.subList(0, realNumbers);

            //步骤7：转成中间表并进行保存
            List<PaperQuestion> paperQuestionList = realQuestionList.stream().map(question ->
                    new PaperQuestion(paper.getId().intValue(), question.getId(), BigDecimal.valueOf(rule.getScore()))
            ).collect(Collectors.toList());
            paperQuestionService.saveBatch(paperQuestionList);
        }
        //3. 修改试卷信息（总题数，总分数）
        paper.setQuestionCount(questionCount);
        paper.setTotalScore(totalScore);
        updateById(paper);
        //4. 返回试卷对象
        return paper;
    }

    @Override
    public List<Paper> listPapers(String name, String status, Long courseId, Long classId, HttpServletRequest request) {
        LambdaQueryWrapper<Paper> queryWrapper = new LambdaQueryWrapper<>();
        if(Objects.equals(userService.getLoginUser(request).getRole(), "teacher")){
            List<Long> classIds = courseService.listCoursesVO(null, request).stream()
                    // 1. 使用 flatMap 将每个 CourseVO 的 classes 列表“拍平”成一个 Class 流
                    //    例如，[[Class1, Class2], [Class3]] 会变成 [Class1, Class2, Class3]
                    .flatMap(course -> course.getClasses().stream())

                    // 2. 从每个 Class 对象中提取 id
                    .map(Class::getId)

                    // 3. 将所有 id 收集到一个 List 中
                    .collect(Collectors.toList());
            QueryWrapper<ClassCourseRelation> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.in("class_id",classIds);
            List<Long> ccIds = classCourseRelationService.list(queryWrapper1).stream().map(ClassCourseRelation::getId).collect(Collectors.toList());
            queryWrapper.in(!ObjectUtils.isEmpty(ccIds), Paper::getClassCourseId,ccIds);
        }
        if(!ObjectUtils.isEmpty(courseId)){
            QueryWrapper<ClassCourseRelation> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("course_id",courseId);
            queryWrapper1.eq(ObjectUtil.isNotEmpty(classId),"class_id",classId);
            Long ccId = classCourseRelationService.getOne(queryWrapper1).getId();
            queryWrapper.eq(!ObjectUtils.isEmpty(ccId),Paper::getClassCourseId,ccId);
        }
        queryWrapper.like(!ObjectUtils.isEmpty(name), Paper::getName, name);
        queryWrapper.eq(!ObjectUtils.isEmpty(status), Paper::getStatus, status);
        List<Paper> paperList = list(queryWrapper);
        log.info("试卷列表接口调用成功！本次条件：name = {} , status = {} , 查询列表为：{}",
                name, status, paperList);
        return paperList;
    }
}