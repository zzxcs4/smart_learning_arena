package com.atguigu.exam.mapper;

import com.atguigu.exam.entity.UserChatRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

/**
* @author zzx
* @description 针对表【user_chat_relation(用户会话关联表（一对多关系）)】的数据库操作Mapper
* @createDate 2025-12-01 18:33:14
* @Entity com.atguigu.exam.entity.UserChatRelation
*/
public interface UserChatRelationMapper extends BaseMapper<UserChatRelation> {

    Integer countByMap(Map map);
}




