package com.zzx.exam.service.impl;

import com.zzx.exam.entity.PaperQuestion;
import com.zzx.exam.mapper.PaperQuestionMapper;
import com.zzx.exam.service.PaperQuestionService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@DS("mysql")
@Service
public class PaperQuestionServiceImpl extends ServiceImpl<PaperQuestionMapper, PaperQuestion> implements PaperQuestionService {

}
