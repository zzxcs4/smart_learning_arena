package com.zzx.exam.springai.rag;

import com.zzx.exam.springai.app.TeachingContentApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TeachingContentAppDocumentLoader {
    private static final Logger log = LoggerFactory.getLogger(TeachingContentApp.class);
    private final ResourcePatternResolver resourcePatternResolver;

    TeachingContentAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            // 这里可以修改为你要加载的多个 Markdown 文件的路径模式
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.docx");

            for (Resource resource : resources) {
                TikaDocumentReader reader = new TikaDocumentReader(resource);
                //System.out.println("resource.getFilename():"+filePath);
                reader.get().forEach(d -> d.mutate().metadata("source",resource.getFilename()));


                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }
}