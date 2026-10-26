package com.zzx.exam.service.impl;

import com.zzx.exam.service.ClassStudentRelationService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.exam.entity.ClassStudentRelation;
import com.zzx.exam.mapper.ClassStudentRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author zzx
* @description 针对表【class_student_relation(班级学生关联表（多对多关系）)】的数据库操作Service实现
* @createDate 2025-11-23 11:14:13
*/
@DS("mysql")
@Service
public class ClassStudentRelationServiceImpl extends ServiceImpl<ClassStudentRelationMapper, ClassStudentRelation>
    implements ClassStudentRelationService {

}




