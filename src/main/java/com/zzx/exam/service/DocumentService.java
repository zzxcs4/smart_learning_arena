package com.zzx.exam.service;

import com.zzx.exam.entity.Document;
import com.zzx.exam.vo.AiGenerateTCRequestVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
* @author zzx
* @description 针对表【document(文档存储表)】的数据库操作Service
* @createDate 2025-11-27 21:31:39
*/
public interface DocumentService extends IService<Document> {

    void addVectorsByd(MultipartFile file, HttpServletRequest request,Long courseId,Integer status);

    void deleteVectorsByFileName(Long documentId);

    String aiGenerateTC(AiGenerateTCRequestVO aiGenerateTCRequestVO);

    String importTc(String teachingContent, Long courseId,HttpServletRequest request);


}
