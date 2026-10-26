package com.zzx.exam.service.postgresql.impl;

import com.zzx.exam.entity.postgresql.AiChatMemory;
import com.zzx.exam.service.postgresql.IAiChatMemoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AiChatMemoryServiceImplTest {
    @Autowired
    private IAiChatMemoryService aiChatMemoryService;
    @Test
    void getConversationList() {
        List<AiChatMemory> conversationList = aiChatMemoryService.getConversationList(List.of("1", "2"));
        System.out.println(conversationList);

    }
}