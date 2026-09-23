package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.AnswerRecord;

import com.atguigu.exam.mapper.AnswerRecordMapper;

import com.atguigu.exam.service.AnswerRecordService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@DS("mysql")
@Service
@Slf4j
public class AnswerRecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord> implements AnswerRecordService {

}
