package com.atguigu.exam.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@DS("postgresql")
@Service
public class VectorService {
    @Autowired
    private VectorStore vectorStore;
    public void extracted(MultipartFile document, String fileUrl, Long courseId) {
        //写入向量数据库
        //1 读取文件
        TikaDocumentReader reader = new TikaDocumentReader(fileUrl);
//            //System.out.println("resource.getFilename():"+filePath);


        //2 文件转换为向量(开启分词)
        List<Document> list = new TokenTextSplitter().transform(reader.get());
        List<org.springframework.ai.document.Document> enhancedDocuments = new ArrayList<>();
        for (org.springframework.ai.document.Document chunk : list) {
            // 创建新的元数据Map
            Map<String, Object> enhancedMetadata = new HashMap<>();
            // 添加您的自定义元数据
            enhancedMetadata.put("course", courseId); // 添加课程分类
            enhancedMetadata.put("source", document.getOriginalFilename()); // 明确标识文档来源
            // 使用mutate()创建携带新元数据的文档
            org.springframework.ai.document.Document enhancedDoc = chunk.mutate()
                    .metadata(enhancedMetadata) // 设置合并后的元数据
                    .build();
            //System.out.println("元数据"+enhancedDoc.getMetadata());
            enhancedDocuments.add(enhancedDoc);
        }


        //3 写入向量数据库
        vectorStore.add(enhancedDocuments);
    }


    public void deleteVectors(com.atguigu.exam.entity.Document document) {
        //删除向量

        Filter.Expression filterExpression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("source"),
                new Filter.Value(document.getFileName())
        );

        vectorStore.delete(filterExpression);
    }

}
