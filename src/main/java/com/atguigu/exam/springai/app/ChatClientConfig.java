package com.atguigu.exam.springai.app;

import com.atguigu.exam.springai.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 配置类
@Configuration
public class ChatClientConfig {
    
    @Bean
    public ChatClient chatClient(ChatModel dashscopeChatModel, ChatMemory chatMemory) {
        String systemProperties = "角色\n你是一位专业的学习助手...";
        
        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemProperties)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new MyLoggerAdvisor()
                )
                .build();
    }
}