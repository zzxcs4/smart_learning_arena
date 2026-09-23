package com.atguigu.exam.mapper.postgresql;


import com.atguigu.exam.entity.postgresql.AiChatMemory;
import com.atguigu.exam.vo.postgresql.ConversationListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * <p>
 * AI对话记忆表 Mapper 接口
 * </p>
 */
@Mapper
public interface AiChatMemoryMapper extends BaseMapper<AiChatMemory> {

    /**
     * 根据会话ID查询对话记录，按时间排序
     */
    @Select("SELECT * FROM ai_chat_memory WHERE conversation_id = #{conversationId} ORDER BY timestamp ASC")
    List<AiChatMemory> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID删除对话记录
     */
    @Select("DELETE FROM ai_chat_memory WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 查询用户的最近会话列表
     */
    @Select("SELECT DISTINCT conversation_id FROM ai_chat_memory ORDER BY timestamp DESC LIMIT #{limit}")
    List<String> selectRecentConversations(@Param("limit") int limit);


}