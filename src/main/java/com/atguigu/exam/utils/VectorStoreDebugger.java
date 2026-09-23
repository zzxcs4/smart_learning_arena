package com.atguigu.exam.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ai.vectorstore.filter.Filter;
import java.util.List;

@Component
public class VectorStoreDebugger {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void debugVectorStore() {
        System.out.println("=== 向量存储诊断 ===");
        
        // 1. 检查向量存储类型
        System.out.println("向量存储类型: " + vectorStore.getClass().getName());
        
        // 2. 搜索一些样本文档查看结构
        SearchRequest request = SearchRequest.builder()
            .query(" ")
            .topK(15)
            .build();
        
        List<Document> samples = vectorStore.similaritySearch(request);
        System.out.println("样本数量: " + samples.size());
        
        for (int i = 0; i < samples.size(); i++) {
            Document doc = samples.get(i);
            System.out.println("--- 文档 " + (i + 1) + " ---");
            System.out.println("文档id: " + doc.getId());
            System.out.println("内容长度: " + doc.getText().length());
            System.out.println("元数据: " + doc.getMetadata());
            System.out.println("所有键: " + doc.getMetadata().keySet());
        }
        
        // 3. 测试特定源的搜索
        String testSource = "your_test_source";
        SearchRequest sourceRequest = SearchRequest.builder()
            .query("*")
            .filterExpression("source == '" + testSource + "'")
            .topK(10)
            .build();
        
        List<Document> sourceDocs = vectorStore.similaritySearch(sourceRequest);
        System.out.println("源 '" + testSource + "' 的文档数量: " + sourceDocs.size());
    }
    
    public void testDeleteWorkflow(String source) {
        System.out.println("=== 测试删除工作流 ===");
        
        // 搜索前计数
        SearchRequest beforeRequest = SearchRequest.builder()
            .query("*")
            .filterExpression("source == '" + source + "'")
            .topK(100)
            .build();
        List<Document> beforeDelete = vectorStore.similaritySearch(beforeRequest);
        System.out.println("删除前文档数: " + beforeDelete.size());
        
        // 执行删除
        try {
            Filter.Expression filterExpression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("source"),
                new Filter.Value(source)
            );
            vectorStore.delete(filterExpression);
            System.out.println("删除命令执行完成");
        } catch (Exception e) {
            System.err.println("删除失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 搜索后计数
        List<Document> afterDelete = vectorStore.similaritySearch(beforeRequest);
        System.out.println("删除后文档数: " + afterDelete.size());
        System.out.println("实际删除数量: " + (beforeDelete.size() - afterDelete.size()));
    }
}