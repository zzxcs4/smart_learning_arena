package com.zzx.exam.service;

import com.zzx.exam.entity.Course;
import com.zzx.exam.vo.CourseQueryRequestVO;
import com.zzx.exam.vo.CourseVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author zzx
* @description 针对表【course(课程表)】的数据库操作Service
* @createDate 2025-11-23 11:03:15
*/
public interface CourseService extends IService<Course> {





    List<Course> listCourses(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request);


    String uploadImage(MultipartFile file);

    List<CourseVO> listCoursesVO(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request);

    void addCourse(Course course,HttpServletRequest request);

    void updateCourse(Course course);

    Course getCourseDetailById(Long id);

    void deleteCourse(Long id);
}
