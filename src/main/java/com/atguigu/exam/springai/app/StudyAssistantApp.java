package com.atguigu.exam.springai.app;

import com.alibaba.cloud.ai.memory.jdbc.PostgresChatMemoryRepository;
import com.atguigu.exam.springai.advisor.MyLoggerAdvisor;
import com.atguigu.exam.springai.rag.TeachingContentAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.messages.AssistantMessage;

import org.springframework.ai.chat.memory.ChatMemory;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Component

public class StudyAssistantApp {
    @Resource
    private VectorStore vectorStore;
    // 和 Spring AI 的工具进行整合
    @Resource
    private ToolCallbackProvider toolCallbackProvider;




    @Resource
    private ChatClient chatClient;
    @Resource
    private ChatMemory chatMemory;

    private static final String SYSTEM_PROPERTIES = "角色\n" +
            "你是一位专业的学习助手，专注于为学生提供高质量的学习支持。你的职责包括作为在线学习助手对学生的问题进行解答。\n" +
            "技能：在线学习助手\n" +
            "任务：回答学生提出的问题。\n" +
            "限制：\n" +
            "保持与用户的良好沟通，确保生成的内容满足用户的需求和期望。\n" +
            "避免使用过于技术性的语言，保持易于理解和访问。\n" +
            "注意事项：\n" +
            "在提供个性化学习建议时，考虑学生的个人情况和学习需求。\n" +
            "保持与学生的良好互动，鼓励他们积极参与学习过程。";



    public Flux<ChatClientResponse>  doChatWithRag(String sessionId,String message,Long status) {
        // 1. 先保存用户消息
        //chatMemory.add(sessionId, List.of(new UserMessage(message)));
        // 2. 创建用于累积响应内容的缓冲区
        //StringBuilder responseBuffer = new StringBuilder();
        // RAG调用逻辑



        // 3. 处理响应流：提取内容并保存
//        StringBuilder fullResponseBuffer = new StringBuilder();
//        fullResponseBuffer.append(chatClientResponseFlux);
//        chatClientResponseFlux// 处理每个ChatClientResponse块
//                .handle((chatClientResponse, sink) -> {
//                    // 3.1 提取本块生成的文本内容并发射出去（给前端）
//                    // chatResponse() 获取的是 ChatResponse 对象
//                    ChatResponse chatResponse = chatClientResponse.chatResponse();
//                    // 从 ChatResponse 中获取文本内容
//                    String chunkContent = chatResponse.getResult() != null
//                            && chatResponse.getResult().getOutput() != null
//                            ? chatResponse.getResult().getOutput().getText()
//                            : "";
//
//                    if (StringUtils.hasText(chunkContent)) {
//                        // 将本块文本内容发射给下游（即返回给前端的Flux）
//                        sink.next(chunkContent);
//                        // 累积到完整响应缓冲区
//
//                    }
//
//
//                });
        return chatClient
                .prompt()
                .system(SYSTEM_PROPERTIES)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                .user(message)
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（知识库服务）
                .advisors(TeachingContentAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                        vectorStore, status
                ))
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, sessionId)
                )

                .stream()
                .chatClientResponse();

    }
    public Flux<String>  doChatWithRags(String sessionId,String message,Long status) {

        return chatClient
                .prompt()
                .system(SYSTEM_PROPERTIES)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                .user(message)
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（知识库服务）
                .advisors(TeachingContentAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                        vectorStore, status
                ))
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, sessionId)
                )

                .stream()
                .content();

    }
    @Resource
    private ToolCallback[] allTools;
    public Flux<String>   doChatWithMcp(String sessionId,String message,Long status) {
        // 1. 先保存用户消息
        //chatMemory.add(sessionId, List.of(new UserMessage(message)));
        // 2. 创建用于累积响应内容的缓冲区
        //StringBuilder responseBuffer = new StringBuilder();
        // RAG调用逻辑


        return chatClient
                .prompt()
                .system(SYSTEM_PROPERTIES)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                .user(message)
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（知识库服务）
//                .advisors(TeachingContentAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                        vectorStore, status
//                ))
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, sessionId)
                )
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();








    }
    public String  doChatWithRag1(String sessionId,String message,Long status) {
        // 1. 先保存用户消息
        //chatMemory.add(sessionId, List.of(new UserMessage(message)));
        // 2. 创建用于累积响应内容的缓冲区
        //StringBuilder responseBuffer = new StringBuilder();
        // RAG调用逻辑


        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROPERTIES)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                .user(message)
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（知识库服务）
//                .advisors(TeachingContentAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                        vectorStore, status
//                ))
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, sessionId)
                )
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        //System.out.println(content);
        log.info("content:{}",content);
        return content;





    }



}
