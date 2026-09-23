package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.Class;
import com.atguigu.exam.entity.ClassCourseRelation;
import com.atguigu.exam.entity.User;
import com.atguigu.exam.service.*;
import com.atguigu.exam.vo.CourseQueryRequestVO;
import com.atguigu.exam.vo.CourseVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.exam.entity.Course;
import com.atguigu.exam.mapper.CourseMapper;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author zzx
* @description 针对表【course(课程表)】的数据库操作Service实现
* @createDate 2025-11-23 11:03:15
*/
@DS("mysql")
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService{
    @Resource
    private UserService userService;
    @Resource
    private ClassCourseRelationService classCourseRelationService;
    @Resource
    private ClassService classService;
    @Resource
    private FileUploadService fileUploadService;
    @Override
    public List<Course> listCourses(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request) {
//        if (courseQueryRequestVO == null) {
//            return new ArrayList<>();
//        }
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        // 从对象中取值
        //对老师：根据当前登录用户id来筛选课程即可

        User loginUser = userService.getLoginUser(request);
        if (Objects.equals(loginUser.getRole(), "teacher")) {
            queryWrapper.eq(ObjectUtils.isNotEmpty(loginUser.getId()),"create_id", loginUser.getId());
        }
        // 2. 安全地处理查询条件
        if (courseQueryRequestVO != null) { // <--- 关键：先判断是否为null
            String courseName = courseQueryRequestVO.getCourseName();
            String courseIntro = courseQueryRequestVO.getCourseIntro();
            String semester = courseQueryRequestVO.getSemester();
            String teacherName = courseQueryRequestVO.getTeacherName();

            // 注意：课程简介通常是模糊查询，使用 like
            queryWrapper.like(StrUtil.isNotBlank(courseName), "courseName", courseName);
            queryWrapper.like(StrUtil.isNotBlank(courseIntro), "courseIntro", courseIntro); // 修改为 like
            queryWrapper.eq(StrUtil.isNotBlank(semester), "semester", semester);
            queryWrapper.eq(StrUtil.isNotBlank(teacherName), "teacherName", teacherName);
        }




        List<Course> courseslist = list(queryWrapper);


        return courseslist;
    }
    @Override
    public String uploadImage(MultipartFile file) {
        //1. 非空校验
        if (file.isEmpty()) {
            //配合全局异常处理，快速返回失败结果！！
            throw new RuntimeException("请选择要上传的文件！");
        }
        //2. 图片格式校验
        //获取文件的mimetype类型
        String contentType = file.getContentType();
        if (ObjectUtils.isEmpty(contentType) || !contentType.startsWith("image")) {
            //配合全局异常处理，快速返回失败结果！！
            throw new RuntimeException("课程封面只能上传图片文件！");
        }
        //3. 文件大小限制
        if (file.getSize() > 5 * 1024 * 1024) {
            //配合全局异常处理，快速返回失败结果！！
            throw new RuntimeException("图片文件大小不能超过5MB");
        }
        //4. 调用文件上传业务
        String imgUrl = fileUploadService.uploadFile(file, "course-cover");
        //5. 返回结果
        log.info("完成课程封面上传，图片回显地址：{}",imgUrl);
        return imgUrl;
    }
    @Override
    public List<CourseVO> listCoursesVO(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request) {
        // 条件查询后的课程列表先装入课程VO列表
        List<CourseVO> coursesVOlist = listCourses(courseQueryRequestVO, request).stream()
                .map(course -> {
                    CourseVO courseVO = new CourseVO();
                    BeanUtils.copyProperties(course, courseVO);
                    return courseVO;
                })
                .collect(Collectors.toList());

        //课程id列表得课程id
        for (CourseVO courseVO : coursesVOlist) {
            //课程id得班级id列表
            QueryWrapper<ClassCourseRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ObjectUtils.isNotEmpty(courseVO.getId()),"course_id", courseVO.getId());

            List<Map<String, Object>> maps = classCourseRelationService.list(queryWrapper).stream().map(q -> {
                // 创建新的Map对象
                Map<String, Object> map = new HashMap<>();
                map.put("classId",q.getClassId()); // 添加ID字段
                map.put("invitationCode",q.getInvitationCode());
                return map;
            }).collect(Collectors.toList());
            if(maps.size()>0){
                List<Class> classes =new ArrayList<>();
                for (Map<String, Object> map : maps) {
                    //班级id列表得班级列表
                    Class clazz = classService.getById((Serializable) map.get("classId"));
                    //装入邀请码
                    if(map.get("invitationCode")!=null){
                    clazz.setInvitationCode(map.get("invitationCode").toString());}
                    //班级列表装入一个CourseVO
                    classes.add(clazz);
                }
                courseVO.setClasses(classes);
            }


        }
        return coursesVOlist;
    }

    @Override
    public void addCourse(Course course ,HttpServletRequest request) {
        if(course==null){
            throw new RuntimeException();
        }
        Long id = userService.getLoginUser(request).getId();
        course.setCreateId(id);
        save(course);
    }

    @Override
    public void updateCourse(Course course) {
        if(course==null){
            throw new RuntimeException();
        }
        updateById(course);
    }

    @Override
    public Course getCourseDetailById(Long id) {
        if(id==0){
            return null;
        }
        return getById(id);
    }

    @Override
    public void deleteCourse(Long id) {
        if(id==0){
            throw new RuntimeException();
        }
        removeById(id);
    }


}




