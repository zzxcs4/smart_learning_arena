package com.zzx.exam.controller;

import com.zzx.exam.common.Result;
import com.zzx.exam.entity.Class;
import com.zzx.exam.entity.ClassCourseRelation;
import com.zzx.exam.service.ClassCourseRelationService;
import com.zzx.exam.service.ClassService;
import com.zzx.exam.utils.InviteCodeGenerator;
import com.atguigu.exam.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzx.exam.vo.ClassAddVO;
import com.zzx.exam.vo.ClassVO;
import com.zzx.exam.vo.CourseQueryRequestVO;
import com.zzx.exam.vo.InvitationCodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/class")  // 课程API路径前缀
@Tag(name = "班级管理", description = "班级相关操作，包括课程的增删改查")  // Swagger API分组
@Slf4j
public class ClassController {
    @Autowired
    @Lazy
    private ClassService classService;
    @Autowired
    private ClassCourseRelationService classCourseRelationService;
    //对学生，根据学生班级关系筛选出班级id列表，根据班级id向上获取数据返回（）
    @GetMapping  // 处理GET请求
    @Operation(summary = "获取班级课程列表(学生)", description = "获取所有加入的课程班级列表，包含每个班级上的课程信息")  // API描述
    public Result<List<ClassVO>> getJoinCourses(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request) {
        // 调用Service层获取处理后的数据
        List<ClassVO> allCourses = classService.listCoursesVO(courseQueryRequestVO,request);
        // 封装成统一响应格式返回
        return Result.success(allCourses);
    }
    /**
     * 添加课程
     * @param clazz 课程对象
     * @return 操作结果
     */
    @PostMapping  // 处理POST请求
    @Operation(summary = "添加新班级", description = "创建新的课程班级，支持设置父课程实现层级结构")  // API描述
    public Result<Void> addCategory(@RequestBody ClassAddVO clazz, HttpServletRequest request) {

        classService.addCourseClass(clazz,request);
        return Result.success(null); // 通常创建操作成功后无需返回特定数据
    }
    @PostMapping("join")  // 处理POST请求
    @Operation(summary = "加入新课程班级", description = "创建新的课程班级，支持设置父课程实现层级结构")  // API描述
    public Result<Void> joinCourseClass(@RequestBody InvitationCodeVO InvitationCode, HttpServletRequest request) {
        classService.joinCourseClass(InvitationCode,request);
        return Result.success(null); // 通常创建操作成功后无需返回特定数据
    }
    /**
     * 更新班级
     * @param classs 班级对象
     * @return 操作结果
     */
    @PutMapping  // 处理PUT请求
    @Operation(summary = "更新班级信息", description = "修改班级的信息")  // API描述
    public Result<Void> updateClass(@RequestBody Class classs, HttpServletRequest request) {
        if(classs==null){
            throw new RuntimeException();
        }
        classService.updateById(classs);

        return Result.success("更新班级接口调用成功！");
    }
    /**
     * 更新班级
     * @return 操作结果
     */
    @GetMapping ("/generateInviteCode") // 处理PUT请求
    @Operation(summary = "生成邀请码", description = "生成邀请码接口")  // API描述
    public Result<String> createInvitationCode(Long courseId,Long classId, HttpServletRequest request) {
        String inviteCode = InviteCodeGenerator.generateInviteCode();
        QueryWrapper<ClassCourseRelation> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("class_id",classId);
        queryWrapper.eq("course_id",courseId);
        ClassCourseRelation one = classCourseRelationService.getOne(queryWrapper);
        one.setInvitationCode(inviteCode);
        classCourseRelationService.updateById(one);
        return Result.success(inviteCode,"生成邀请码接口调用成功！");
    }
}
