package com.atguigu.exam.vo.postgresql;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationListVO {
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 对话标题（第一条用户消息的摘要）
     */
    private String title;
    
    /**
     * 第一条消息内容（完整内容）
     */
    private String firstMessage;
    
    /**
     * 对话开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 消息总数
     */
    private Integer messageCount;
    
    /**
     * 用户消息数量
     */
    private Integer userMessageCount;
    
    /**
     * 助手消息数量
     */
    private Integer assistantMessageCount;
}