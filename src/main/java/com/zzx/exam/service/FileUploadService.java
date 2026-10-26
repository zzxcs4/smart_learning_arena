package com.zzx.exam.service;


import com.zzx.exam.vo.PutObjectResultVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务
 * 支持MinIO和本地文件存储两种方式
 */

public interface FileUploadService {
    String uploadFile(MultipartFile multipartFile, String folder);

    String uploadFileByContent(String teachingContent, Long userId);

    PutObjectResultVO uploadFilePlus(MultipartFile multipartFile, String folder);
}