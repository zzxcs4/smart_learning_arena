package com.zzx.exam.controller;

import com.alibaba.cloud.ai.memory.jdbc.PostgresChatMemoryRepository;
import com.zzx.exam.springai.app.StudyAssistantApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
class ChatControllerTest {
//    @Autowired
//    private StudyAssistantApp studyAssistantApp;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private StudyAssistantApp studyAssistantApp;
    @Test
    void doChatWithStudyAssistant() {
        //String answer;
        //Assertions.assertNotNull(studyAssistantApp.doChatWithRag1("187","帮我搜索几个关于“web开发”的页面",1L));
        //studyAssistantApp.doChatWithRag("91","什么是web",1L);
        //System.out.println(answer);
        Assertions.assertNotNull(studyAssistantApp.doChatWithRag1("1","可以做什么",1L));
        //Assertions.assertNotNull(answer);
        //System.out.println(answer);
    }
    @Test
    void testJdbcTemplate() {
        DataSource dataSource = jdbcTemplate.getDataSource();
        System.out.println(jdbcTemplate);
    }
    @Test
    void testChatMemory() {

        log.info("开始创建 ChatMemory Bean...");
        // 检查 jdbcTemplate 是否为空
        if (jdbcTemplate == null) {
            log.error("jdbcTemplate 是 null，无法创建 ChatMemory");

        }

        try {
            log.info("尝试构建 PostgresChatMemoryRepository...");
            var repository = PostgresChatMemoryRepository.postgresBuilder()
                    .jdbcTemplate(jdbcTemplate)
                    .build();
            log.info("PostgresChatMemoryRepository 构建成功");

            var chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(repository)
                    .maxMessages(100)
                    .build();
            log.info("ChatMemory 创建成功");

        } catch (Exception e) {
            log.error("创建 ChatMemory 失败", e);

        }
    }
    @Test
    void testChatMemory2() {
        ChatMemory chatMemory1 = chatMemory;
        System.out.println(chatMemory1);

    }
}