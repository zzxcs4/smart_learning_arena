package com.atguigu.exam.springai.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {


    private String searchApiKey="L7zqrdkiLN1T5QLrpPtLtMMi";

    @Bean
    public ToolCallback[] allTools() {

        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);

        return ToolCallbacks.from(

            webSearchTool

        );
    }
}
