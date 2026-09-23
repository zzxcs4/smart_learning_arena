package com.atguigu.exam.service;


import com.atguigu.exam.vo.StatisticsCountVO;
import com.atguigu.exam.vo.StatsVo;

import java.time.LocalDateTime;

/**
 * 统计数据服务接口
 */
public interface StatsService {
    
    /**
     * 获取系统统计数据
     * @return 统计数据DTO
     */
    StatsVo getSystemStats();

    StatisticsCountVO getDashboardStats(LocalDateTime begin, LocalDateTime end);
}