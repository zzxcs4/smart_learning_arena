package com.zzx.exam.controller;

import com.zzx.exam.common.Result;
import com.zzx.exam.entity.Course;
import com.zzx.exam.service.CourseService;
import com.zzx.exam.service.UserService;
import com.zzx.exam.vo.CourseQueryRequestVO;
import com.zzx.exam.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/course")  // 课程API路径前缀
@Tag(name = "课程管理", description = "课程相关操作，包括课程的增删改查")  // Swagger API分组
@Slf4j
public class CourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private UserService userService;
    /**
     * 获取课程列表（包含题目数量）
     * @return 课程列表数据
     */
    @GetMapping ("admin") // 处理GET请求
    @Operation(summary = "获取课程列表(管理员)", description = "获取所有课程列表")  // API描述
    public Result<List<Course>> getCourses(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request) {
        // 调用Service层获取处理后的数据
        List<Course> allCourses = courseService.listCourses(courseQueryRequestVO,request);
        // 封装成统一响应格式返回
        return Result.success(allCourses);
    }
    @GetMapping  // 处理GET请求
    @Operation(summary = "获取课程列表(教师)", description = "获取所有创建的课程列表，包含每个课程下的班级信息")  // API描述
    public Result<List<CourseVO>> getCreateCourses(CourseQueryRequestVO courseQueryRequestVO, HttpServletRequest request) {
        // 调用Service层获取处理后的数据
        List<CourseVO> allCourses = courseService.listCoursesVO(courseQueryRequestVO,request);
        // 封装成统一响应格式返回
        return Result.success(allCourses);
    }



    /**
     * 添加课程
     * @param course 课程对象
     * @return 操作结果
     */
    @PostMapping  // 处理POST请求
    @Operation(summary = "添加新课程", description = "创建新的题目课程，支持设置父课程实现层级结构")  // API描述
    public Result<Void> addCategory(@RequestBody Course course,HttpServletRequest request) {
        courseService.addCourse(course,request);
        return Result.success(null); // 通常创建操作成功后无需返回特定数据
    }
    /**
     * 上传课程图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/upload-image")  // 处理POST请求
    @Operation(summary = "上传课程封面图片", description = "将图片文件上传到MinIO服务器，返回可访问的图片URL")  // API描述
    public Result<String> uploadBannerImage(
            @Parameter(description = "要上传的图片文件，支持jpg、png、gif等格式，大小限制5MB")
            @RequestParam("file") MultipartFile file) {
        String imgUrl = courseService.uploadImage(file);

        return Result.success(imgUrl, "图片上传成功");
    }
    /**
     * 更新课程
     * @param course 课程对象
     * @return 操作结果
     */
    @PutMapping  // 处理PUT请求
    @Operation(summary = "更新课程信息", description = "修改课程的名称、描述、排序等信息")  // API描述
    public Result<Void> updateCourse(@RequestBody Course course,HttpServletRequest request) {
        courseService.updateCourse(course);

        return Result.success("更新课程接口调用成功！");
    }
    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")  // 处理GET请求
    @Operation(summary = "获取课程详情", description = "获取课程的详细信息")  // API描述
    public Result<Course> getPaperById(@Parameter(description = "试卷ID") @PathVariable Long id) {
        Course course =  courseService.getCourseDetailById(id);
        course.setCreateName( userService.getById(course.getCreateId()).getRealName());
        log.info("查询课程详情接口成功！课程信息为:{}",course);
        return Result.success(course);
    }
    /**
     * 删除课程
     * @param id 课程ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")  // 处理DELETE请求
    @Operation(summary = "删除课程", description = "删除指定的题目课程，注意：删除前需确保课程下没有题目")  // API描述
    public Result<Void> deleteCategory(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success(null);
    }
}
