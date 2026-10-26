package com.zzx.exam.controller;

import com.zzx.exam.service.DocumentService;
import com.zzx.exam.springai.rag.TeachingContentAppDocumentLoader;
import com.zzx.exam.utils.VectorStoreDebugger;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class DocumentControllerTest {
    @Resource
    private DocumentService documentVectorService;
    @Resource
    private TeachingContentAppDocumentLoader teachingContentAppDocumentLoader;
    @Resource
    private VectorStore vectorStore;
    @Resource
    private VectorStoreDebugger vectorStoreDebugger;
    @Test
    void deleteDocument() {
        documentVectorService.deleteVectorsByFileName(1L);
    }
    @Test
    void getDocuments() {
        List<Document> documentList = teachingContentAppDocumentLoader.loadMarkdowns();
        List<Document> list = new TokenTextSplitter().transform(documentList);
        //3 手动构建id,方便等会删除
        List<String> IdList = list.stream().map(Document::getId).collect(Collectors.toList());
        System.out.println(IdList);


    }
    @Test
    void updateDocument() {}
    @Test
    void setVectorStoreByUrl() {
        // 1. 读取文档并分词
       // TikaDocumentReader reader = new TikaDocumentReader("https://teaching-training-1350469262.cos.ap-beijing.myqcloud.com/knowledge-base/第二章 输入输出.pptx");

        List<Document> documentList = teachingContentAppDocumentLoader.loadMarkdowns();
        List<Document> chunks = new TokenTextSplitter().transform(documentList);

        List<Document> enhancedDocuments = new ArrayList<>();
        String fileSource = "恋爱常见问题和回答 - 单身篇.docx"; // 定义source，用于后续删除

// 2. 为每个文档块增强元数据
        for (Document chunk : chunks) {
            // 创建新的元数据Map，包含原始元数据和新增内容
            Map<String, Object> enhancedMetadata = new HashMap<>();

            // 保留原始文档的所有元数据
            enhancedMetadata.putAll(chunk.getMetadata());

            // 添加您的自定义元数据
            enhancedMetadata.put("course", 1); // 添加课程分类
            enhancedMetadata.put("source", fileSource); // 明确标识文档来源

            // 使用mutate()创建携带新元数据的文档
            Document enhancedDoc = chunk.mutate()
                    .metadata(enhancedMetadata) // 设置合并后的元数据
                    .build();
            System.out.println("元数据"+enhancedDoc.getMetadata());
            enhancedDocuments.add(enhancedDoc);
        }

// 3. 存入向量数据库
        vectorStore.add(enhancedDocuments);
    }
    @Test
    void VectorStoreDebugger(){
        vectorStoreDebugger.debugVectorStore();
    }
}