package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.PaperQuestion;
import com.atguigu.exam.mapper.PaperQuestionMapper;
import com.atguigu.exam.service.PaperQuestionService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@DS("mysql")
@Service
public class PaperQuestionServiceImpl extends ServiceImpl<PaperQuestionMapper, PaperQuestion> implements PaperQuestionService {

}
