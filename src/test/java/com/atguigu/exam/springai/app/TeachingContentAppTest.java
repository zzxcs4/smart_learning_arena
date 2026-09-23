package com.atguigu.exam.springai.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TeachingContentAppTest {
    @Resource
    private TeachingContentApp teachingContentApp;
    @Resource
    private StudyAssistantApp studyAssistantApp;
    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "生成“认识web应用”的教案";
        String answer = teachingContentApp.doChatWithRagCloud(message,1);
        Assertions.assertNotNull(answer);

    }
    @Test
    void testChat2() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "生成“认识web应用”的教案";
        String answer = teachingContentApp.doChatWithRag(message,1,1L);
        Assertions.assertNotNull(answer);

    }


}
