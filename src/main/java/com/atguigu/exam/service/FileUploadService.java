package com.atguigu.exam.service;


import com.atguigu.exam.vo.PutObjectResultVO;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件上传服务
 * 支持MinIO和本地文件存储两种方式
 */

public interface FileUploadService {
    String uploadFile(MultipartFile multipartFile, String folder);

    String uploadFileByContent(String teachingContent, Long userId);

    PutObjectResultVO uploadFilePlus(MultipartFile multipartFile, String folder);
}