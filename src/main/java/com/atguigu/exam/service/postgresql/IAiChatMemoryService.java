package com.atguigu.exam.service.postgresql;

import com.atguigu.exam.entity.postgresql.AiChatMemory;
import com.atguigu.exam.vo.postgresql.ConversationListVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * <p>
 * AI对话记忆表 服务类
 * </p>
 */
public interface IAiChatMemoryService extends IService<AiChatMemory> {

    /**
     * 获取对话列表（分页）
     */
    List<AiChatMemory> getConversationList(List<String> sessionIds);
    /**
     * 根据会话ID获取对话历史
     */
    List<AiChatMemory> getConversationHistory(String conversationId);

    /**
     * 保存对话消息
     */
    boolean saveMessage(String conversationId, String content, AiChatMemory.MessageType type);

    /**
     * 删除会话记录
     */
    boolean deleteConversation(String conversationId);

    /**
     * 获取用户最近会话
     */
    List<String> getRecentConversations(int limit);
}