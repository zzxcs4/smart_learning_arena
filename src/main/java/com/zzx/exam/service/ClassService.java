package com.zzx.exam.service;

import com.zzx.exam.entity.Class;
import com.zzx.exam.vo.ClassAddVO;
import com.zzx.exam.vo.ClassVO;
import com.zzx.exam.vo.CourseQueryRequestVO;
import com.zzx.exam.vo.InvitationCodeVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author zzx
* @description 针对表【class(班级表)】的数据库操作Service
* @createDate 2025-11-23 11:04:40
*/
public interface ClassService extends IService<Class> {

    List<ClassVO> listCoursesVO(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request);

    void addCourseClass(ClassAddVO clazz, HttpServletRequest request);

    void joinCourseClass(InvitationCodeVO invitationCode, HttpServletRequest request);
}
