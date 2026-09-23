package com.atguigu.exam.controller;

import com.atguigu.exam.springai.advisor.MyLoggerAdvisor;
import com.atguigu.exam.springai.app.StudyAssistantApp;
import com.atguigu.exam.vo.postgresql.AIChatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/rag")
@Tag(name = "RAG管理", description = "RAG相关操作，包括测试RAG")
public class RagController
{

    @Resource(name = "qwenChatClient")
    private ChatClient chatClient;
    @Resource
    private VectorStore vectorStore;
    @Resource
    private StudyAssistantApp studyAssistantApp;
    /**
     * http://localhost:8012/rag4aiops?msg=00000
     * http://localhost:8012/rag4aiops?msg=C2222
     * @param msg
     * @return
     */
    @GetMapping(value = "/rag4aiops", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    @Operation(summary = "RAG对话(老师)", description = "根据知识库回答")  // API描述
    public String rag(String msg)
    {
        String systemInfo = """
                你是一个学习助手,按照给出的相关知识给出对应解释,否则回复找不到信息。
                """;

        RetrievalAugmentationAdvisor advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore).build())
                .build();

        return chatClient
                .prompt()
                .system(systemInfo)
                .user(msg)
                .advisors(advisor)
                .call()
                .chatResponse().getResult().getOutput().getText();
    }
    @GetMapping(value = "/rags", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    @Operation(summary = "RAG对话流式(学生)", description = "根据知识库回答")  // API描述
    public Flux<ChatClientResponse> rags()
    {


        return studyAssistantApp.doChatWithRag("92","什么是web",1L);
    }
    @PostMapping(value = "/ragss", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    @Operation(summary = "RAG对话流式(学生)", description = "根据知识库回答")  // API描述
    public Flux<String> ragss(@RequestBody AIChatVO aiChatVO, HttpServletRequest request)
    {


        return studyAssistantApp.doChatWithRags(aiChatVO.getConversationId(),aiChatVO.getQuery(),aiChatVO.getCourseId());
    }


    @PostMapping(value = "/mcp", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    @Operation(summary = "RAG对话流式(学生)", description = "网页搜索回答")  // API描述
    public Flux<String> mcp(@RequestBody AIChatVO aiChatVO, HttpServletRequest request)
    {


        return studyAssistantApp.doChatWithMcp(aiChatVO.getConversationId(),aiChatVO.getQuery(),aiChatVO.getCourseId());
    }
}