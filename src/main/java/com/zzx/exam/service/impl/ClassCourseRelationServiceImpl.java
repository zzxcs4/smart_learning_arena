package com.zzx.exam.service.impl;

import com.zzx.exam.service.ClassCourseRelationService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.exam.entity.ClassCourseRelation;
import com.zzx.exam.mapper.ClassCourseRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author zzx
* @description 针对表【class_course_relation(班级课程关联表（多对多关系）)】的数据库操作Service实现
* @createDate 2025-11-23 11:05:18
*/
@DS("mysql")
@Service
public class ClassCourseRelationServiceImpl extends ServiceImpl<ClassCourseRelationMapper, ClassCourseRelation>
    implements ClassCourseRelationService {

}




