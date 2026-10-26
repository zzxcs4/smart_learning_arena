package com.zzx.exam.service.impl;

import com.zzx.exam.entity.ExamRecord;
import com.zzx.exam.mapper.ExamRecordMapper;
import com.zzx.exam.service.ExamRecordService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 考试记录Service实现类
 * 实现考试记录相关的业务逻辑
 */
@DS("mysql")
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {
    

} 