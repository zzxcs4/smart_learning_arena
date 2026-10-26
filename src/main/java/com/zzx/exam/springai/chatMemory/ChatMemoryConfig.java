package com.zzx.exam.springai.chatMemory;

import com.alibaba.cloud.ai.memory.jdbc.PostgresChatMemoryRepository;
import com.qcloud.cos.COSClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ChatMemoryConfig  {

    @Bean
    public ChatMemory chatMemory(JdbcTemplate jdbcTemplate) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(PostgresChatMemoryRepository.postgresBuilder()
                        .jdbcTemplate(jdbcTemplate)
                        .build())
                .maxMessages(100)
                .build();
    }

}