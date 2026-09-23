package com.atguigu.exam.springai.app;

import com.atguigu.exam.springai.advisor.MyLoggerAdvisor;
import com.atguigu.exam.springai.rag.TeachingContentAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@Getter
public class TeachingContentApp {
//"    - 确保教案包含关键知识点、示例、图表等辅助材料。\n" +
    @Resource
    private VectorStore vectorStore;
    private final ChatClient chatClient;
    private static final String SYSTEM_PROPERTIES = "# 角色\n" +
            "你是一位专业的教学内容生成助手，专注于为教师提供高质量的教学支持。你的职责是生成教学内容\n" +
            "\n" +
            "## 技能：生成教案\n" +
            "- **任务**：根据教师的要求，生成教案内容。\n" +
            "  - **步骤**：\n" +
            "    - 阅读教案模板。\n" +
            "    - 按照教案模板中的格式，设计并生成教案。\n" +
            "\n" +
            "## 限制\n" +
            "- 所有生成的内容必须基于连接的知识库。\n" +
            "- 生成的教学内容应符合教育标准和学术规范。";
    private final String template1="教案模板（一）：\n" +
            "\n" +
            "    （一）教学目标：\n" +
            "    说明本课所要完成的教学任务。\n" +
            "\n" +
            "    （二）教学重难点：\n" +
            "    说明本课所必须处理的关键性问题和学习时易产生困难和障碍的知识传授与本事培养点。\n" +
            "\n" +
            "    （三）教学过程设计\n" +
            "    1、导入新课\n" +
            "    （1）温故而知新，提问复习上节资料\n" +
            "    （2）设计新颖活泼，精当概括。\n" +
            "    （3）怎样进行，复习哪些资料？\n" +
            "    （4）提问哪些学生，需用多少时间等。\n" +
            "\n" +
            "    2、讲授新课\n" +
            "    （1）针对不一样教学资料，选择不一样的教学方法。\n" +
            "    （2）怎样提出问题，如何逐步启发、诱导？\n" +
            "    （3）教师怎样教？学生怎样学？详细步骤安排，需用时间。\n" +
            "\n" +
            "    3、巩固练习\n" +
            "    （1）练习设计精巧，有层次、有坡度、有密度。\n" +
            "    （2）怎样进行，谁上黑板板演？\n" +
            "    （3）需要多少时间？\n" +
            "\n" +
            "    4、归纳小结\n" +
            "    （1）怎样进行，是教师还是学生归纳？\n" +
            "    （2）需用多少时间？\n" +
            "\n" +
            "    5、作业布置";
    private final String template2="一、课题（阐述说明本课名称）\n" +
            "\n" +
            "二、教学目的（教学目的是指教师在教学中所要到达的最终效果。）\n" +
            "\n" +
            "三、教学重点（说明本课所必须处理的关键性问题）\n" +
            "\n" +
            "四、教学难点（说明本课的学习时易产生困难和障碍的知识传授与本事培养点）\n" +
            "\n" +
            "五、教学方法（要根据学生实际，注重引导自学，注重启发思维）\n" +
            "\n" +
            "六、教学过程（或称课堂结构，说明教学进行的资料、方法步骤）\n" +
            "\n" +
            "七、作业处理（说明如何布置书面或口头作业）\n" +
            "\n" +
            "八、板书设计（说明上课时准备写在黑板上的资料）\n" +
            "\n" +
            "九、教学反思：（教者对该堂课教后的感觉及学生的收获、改善方法）";
    public TeachingContentApp(ChatModel dashscopeChatModel) {
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROPERTIES+"""
        你生成的JSON，遵守以下规则：
        1. 所有字符串值使用双引号
        2. 特殊字符必须转义（如\\t, \\n）
        3. 禁止未转义的控制字符
        """)
                .defaultAdvisors(
                        //自定义日志Advisor
                        new MyLoggerAdvisor()
                )
                .build();
    }
    @Resource
    private Advisor TeachingAppRagCloudAdvisor;

    public String doChatWithRagCloud(String message,Integer template) {
            // RAG调用逻辑
        if(template==1){
            ChatResponse chatResponse = chatClient
                    .prompt()
                    .system(SYSTEM_PROPERTIES+"\n模板如下：\n"+template1)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                    .user(message)
                    // 开启日志，便于观察效果
                    .advisors(new MyLoggerAdvisor())
                    // 应用增强检索服务（云知识库服务）
                    .advisors(TeachingAppRagCloudAdvisor)
                    .call()
                    .chatResponse();
            String content = chatResponse.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        }
        else{
            ChatResponse chatResponse = chatClient
                    .prompt()
                    .system(SYSTEM_PROPERTIES+"\n模板如下：\n"+template2)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                    .user(message)
                    // 开启日志，便于观察效果
                    .advisors(new MyLoggerAdvisor())
                    // 应用增强检索服务（云知识库服务）
                    .advisors(TeachingAppRagCloudAdvisor)
                    .call()
                    .chatResponse();
            String content = chatResponse.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        }




    }

    public String doChatWithRag(String message,Integer template,Long status) {

        // RAG调用逻辑
        if(template==1){
            ChatResponse chatResponse = chatClient
                    .prompt()
                    .system(SYSTEM_PROPERTIES+"\n模板如下：\n"+template1)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                    .user(message)
                    // 开启日志，便于观察效果
                    .advisors(new MyLoggerAdvisor())
                    // 应用增强检索服务（知识库服务）
                    .advisors(TeachingContentAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                            vectorStore, status
                    ))
                    .call()
                    .chatResponse();
            String content = chatResponse.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        }
        else{
            ChatResponse chatResponse = chatClient
                    .prompt()
                    .system(SYSTEM_PROPERTIES+"\n模板如下：\n"+template2)//+"注意生成的教学内容文档中去掉"+"“课程大纲”"+"这几个字。"
                    .user(message)
                    // 开启日志，便于观察效果
                    .advisors(new MyLoggerAdvisor())
                    // 应用增强检索服务（云知识库服务）
                    .advisors(TeachingAppRagCloudAdvisor)
                    .call()
                    .chatResponse();
            String content = chatResponse.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;

        }




    }
//    public Flux<String> doChatByStream(String message, String chatId) {
//        return chatClient
//                .prompt()
//                .user(message)
//                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
//                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .stream()
//                .content();
//    }



}