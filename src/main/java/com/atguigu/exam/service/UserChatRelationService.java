package com.atguigu.exam.service;

import com.atguigu.exam.entity.UserChatRelation;
import com.atguigu.exam.vo.postgresql.AIChatVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author zzx
* @description 针对表【user_chat_relation(用户会话关联表（一对多关系）)】的数据库操作Service
* @createDate 2025-12-01 18:33:14
*/
public interface UserChatRelationService extends IService<UserChatRelation> {

    List<String> listByUserId(Long userId);

    Long beforeDoChat(AIChatVO aIChatVO, HttpServletRequest request);
}
