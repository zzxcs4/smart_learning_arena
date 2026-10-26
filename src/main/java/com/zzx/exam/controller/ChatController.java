package com.zzx.exam.controller;

import com.zzx.exam.common.Result;
import com.zzx.exam.entity.postgresql.AiChatMemory;
import com.zzx.exam.service.UserChatRelationService;
import com.zzx.exam.service.UserService;
import com.zzx.exam.service.postgresql.IAiChatMemoryService;
import com.zzx.exam.springai.app.StudyAssistantApp;
import com.zzx.exam.vo.postgresql.AIChatVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/chat")  // 分类API路径前缀
@Slf4j
public class ChatController {
    @Autowired
    private StudyAssistantApp studyAssistantApp;
    @Autowired
    private IAiChatMemoryService aiChatMemoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserChatRelationService userChatRelationService;

    @PostMapping(value = "/AIChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithStudyAssistant(@RequestBody AIChatVO aiChatVO, HttpServletRequest request) {
        Long courseId = userChatRelationService.beforeDoChat(aiChatVO, request);
        if(aiChatVO.getWebSearch()){
            return studyAssistantApp.doChatWithMcp(aiChatVO.getConversationId(),aiChatVO.getQuery(),courseId);
        }else {
            return studyAssistantApp.doChatWithRags(aiChatVO.getConversationId(),aiChatVO.getQuery(),courseId);
        }


    }

    /**
     * 新增对话
     */
    @PostMapping("/newChat")
    public Result<String> newChat(HttpServletRequest request) {
        Long userId = userService.getLoginUser(request).getId();
        //构建sessionId
        String sessionId = "chat-"+userId.toString()+"-"+System.currentTimeMillis();

        return Result.success(sessionId,"返回成功");
    }
    /**
     * 获取对话历史列表
     * 返回每个对话的第一条用户消息作为标题
     */
    @GetMapping("/conversations")
    public Result<List<AiChatMemory>> getConversationList(HttpServletRequest request) {
        Long id = userService.getLoginUser(request).getId();
        List<String> sessionIds = userChatRelationService.listByUserId(id);
        List<AiChatMemory> conversationList = aiChatMemoryService.getConversationList(sessionIds);

        return Result.success(conversationList,"成功");
    }
    /**
     * 获取对话历史
     */
    @GetMapping("/history/{conversationId}")
    public List<AiChatMemory> getHistory(@PathVariable String conversationId) {
        return aiChatMemoryService.getConversationHistory(conversationId);
    }
    /**
     * 删除对话记录
     */
    @DeleteMapping("/history/{conversationId}")
    public boolean deleteHistory(@PathVariable String conversationId) {
        return aiChatMemoryService.deleteConversation(conversationId);
    }

    /**
     * 获取最近会话
     */
    @GetMapping("/recent-conversations")
    public List<String> getRecentConversations() {
        return aiChatMemoryService.getRecentConversations(10);
    }
}
