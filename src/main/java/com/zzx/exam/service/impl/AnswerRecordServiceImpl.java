package com.zzx.exam.service.impl;

import com.zzx.exam.entity.AnswerRecord;

import com.zzx.exam.mapper.AnswerRecordMapper;

import com.zzx.exam.service.AnswerRecordService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@DS("mysql")
@Service
@Slf4j
public class AnswerRecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord> implements AnswerRecordService {

}
