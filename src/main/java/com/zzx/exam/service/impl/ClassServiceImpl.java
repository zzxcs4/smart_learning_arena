package com.zzx.exam.service.impl;


import com.zzx.exam.entity.*;

import com.zzx.exam.entity.Class;
import com.zzx.exam.service.*;
import com.zzx.exam.vo.ClassAddVO;
import com.zzx.exam.vo.ClassVO;
import com.zzx.exam.vo.CourseQueryRequestVO;
import com.zzx.exam.vo.InvitationCodeVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.exam.mapper.ClassMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zzx
* @description 针对表【class(班级表)】的数据库操作Service实现
* @createDate 2025-11-23 11:04:40
*/
@DS("mysql")
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class>
    implements ClassService {
    @Resource
    private UserService userService;
    @Resource
    private ClassStudentRelationService classStudentRelationService;
    @Resource
    private ClassCourseRelationService classCourseRelationService;
    @Resource
    @Lazy
    private CourseService courseService;

    @Override
    public List<ClassVO> listCoursesVO(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request) {
        List<ClassVO> classVOS=new ArrayList<>();
        User loginUser = userService.getLoginUser(request);
        //对学生，根据学生班级关系筛选出班级id列表，根据班级id向上获取数据返回（）
        //根据学生id获取班级id列表，对每个班级对象进行转换，并获取对应课程信息
        QueryWrapper<ClassStudentRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(loginUser.getId()), "student_id", loginUser.getId());
        List<Long> classesId = classStudentRelationService.list(queryWrapper).stream().map(ClassStudentRelation::getClassId).collect(Collectors.toList());
        //根据班级id从班级课程关系表获取课程id（班级表和班级课程关系表可以合二为一）

        for (Long classId : classesId) {

            QueryWrapper<ClassCourseRelation> queryWrapper1 = new QueryWrapper<>();
            queryWrapper.eq(ObjectUtils.isNotEmpty(loginUser.getId()), "class_id", classId);
            List<Long> courseIds = classCourseRelationService.list(queryWrapper1).stream().map(ClassCourseRelation::getCourseId).collect(Collectors.toList());
            for (Long courseId : courseIds) {
            Course course = courseService.getById(courseId);
                ClassVO classVO = new ClassVO();
                BeanUtils.copyProperties(getById(classId), classVO);
                classVO.setCourseId(courseId);
            classVO.setCourseCover(course.getCourseCover());
            classVO.setCourseIntro(course.getCourseIntro());
            classVO.setCourseName(course.getCourseName());
            classVO.setSemester(course.getSemester());
            classVO.setTeacherName(userService.getById(course.getCreateId()).getRealName());
            classVO.setUserUrl(userService.getById(course.getCreateId()).getUsername());
                classVOS.add(classVO);
            }

        }

        //根据课程id获取课程信息
        return classVOS;
    }

    @Override
    @Transactional
    public void addCourseClass(ClassAddVO clazz, HttpServletRequest request) {
        if(clazz==null){
            throw new RuntimeException("数据为空");
        }

        Class aClass = new Class();
        BeanUtils.copyProperties(clazz,aClass);
        QueryWrapper<Class> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(aClass.getClassName()), "class_name", aClass.getClassName());
        if(getOne(queryWrapper)==null){
        save(aClass);
        }
        Long classId= getOne(queryWrapper).getId();
        ClassCourseRelation classCourseRelation = new ClassCourseRelation();
        classCourseRelation.setCourseId(clazz.getCourseId());
        classCourseRelation.setClassId(classId);
        classCourseRelationService.save(classCourseRelation);



    }
    @Transactional
    @Override
    public void joinCourseClass(InvitationCodeVO invitationCode, HttpServletRequest request) {
        QueryWrapper<ClassCourseRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(invitationCode.getInvitationCode()), "invitation_code", invitationCode.getInvitationCode());
        Long classId = classCourseRelationService.getOne(queryWrapper).getClassId();
        ClassStudentRelation classStudentRelation = new ClassStudentRelation();
        classStudentRelation.setClassId(classId);
        Long userId = userService.getLoginUser(request).getId();
        classStudentRelation.setStudentId(userId);
        classStudentRelationService.save(classStudentRelation);
    }
}




