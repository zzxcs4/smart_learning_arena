package com.zzx.exam.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SaaLLMConfig
{
    // 模型名称常量定义
    private final String DEEPSEEK_MODEL = "deepseek-v3";
    private final String QWEN_MODEL = "qwen3.8-flash";



    @Bean(name = "qwen")
    @Primary
    public ChatModel qwen()
    {
        return DashScopeChatModel.builder().dashScopeApi(DashScopeApi.builder()
                        .apiKey("sk-4304d2a587b543dd8374f8269a7873fc")
                        .build())
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .model(QWEN_MODEL)
                                .maxToken(2048)
                                .withMultiModel(true)
                                .enableThinking(false)
                                .build()
                )
                .build();
    }




    @Bean(name = "qwenChatClient")
    public ChatClient qwenChatClient(@Qualifier("qwen") ChatModel qwen) {
        // 不再重复设置 model，ChatModel 已经绑定了默认值
        return ChatClient.builder(qwen).build();
    }
}