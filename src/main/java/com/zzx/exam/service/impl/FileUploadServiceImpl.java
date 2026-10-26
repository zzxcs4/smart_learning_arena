package com.zzx.exam.service.impl;

import com.zzx.exam.config.CosClientConfig;
import com.zzx.exam.service.FileUploadService;
import com.zzx.exam.vo.PutObjectResultVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;


/**
 * projectName: com.atguigu.exam.service.impl
 *
 * @author: 赵伟风
 * description:
 */
@DS("mysql")
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {


        @Resource
        private CosClientConfig cosClientConfig;

        @Resource
        private COSClient cosClient;

        // ... 一些操作 COS 的方法



    /**
     * 测试文件上传
     *
     * @param multipartFile
     * @return
     */
    @Override
    public String uploadFile(MultipartFile multipartFile,String folder) {
            // 文件目录
            String filename = multipartFile.getOriginalFilename();
            String filepath = String.format("/"+folder+"/%s", filename);
            File file = null;
            try {
                // 上传文件
                file = File.createTempFile(filepath, null);
                multipartFile.transferTo(file);
                PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), filepath,
                        file);
                PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
                // 返回可访问地址

            } catch (Exception e) {
                log.error("file upload error, filepath = " + filepath, e);

            } finally {
                if (file != null) {
                    // 删除临时文件
                    boolean delete = file.delete();
                    if (!delete) {
                        log.error("file delete error, filepath = {}", filepath);
                    }
                }
            }
        return cosClientConfig.getHost()+filepath;

    }

    /**
     * 根据文件内容上传
     * @param multipartFile
     * @param folder
     * @return
     */
    @Override
    public String uploadFileByContent(String teachingContent, Long userId) {
        String fileName = extractFileNameFromFirstLine(teachingContent)
                + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + ".docx";
        String folder="teachingContent"+userId;
        String filepath = String.format("/"+folder+"/%s", fileName);
        // 2. 构建 COS 存储路径（basePath + 文件名）


        // 3. 将字符串转为输入流（无需写入本地文件，直接内存流上传，效率更高）
        try (InputStream inputStream = new ByteArrayInputStream(teachingContent.getBytes("UTF-8"))) {
            // 4. 配置文件元数据（如文件大小、编码）
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            metadata.setContentEncoding("UTF-8");
            metadata.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // 设置文件MIME类型

            // 5. 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosClientConfig.getBucket(), filepath, inputStream, metadata);

            // 6. 上传文件到 COS

            try {
                PutObjectResult result = cosClientConfig.cosClient().putObject(putObjectRequest);
                System.out.println("COS 上传成功，ETag：" + result.getETag());

                // 7. 生成文件访问 URL（带签名，有效期 expireSeconds）
                return cosClientConfig.getHost()+filepath;
            } finally {
                // 7. 关闭客户端（重要：避免资源泄露）
                cosClient.shutdown();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    /**
     * 根据文件后缀获取 Content-Type（用于浏览器正确解析文件）
     */
    private String getContentType(String fileSuffix) {
        return switch (fileSuffix.toLowerCase()) {
            case ".txt" -> "text/plain; charset=UTF-8";
            case ".md" -> "text/markdown; charset=UTF-8";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".pdf" -> "application/pdf";
            default -> "application/octet-stream"; // 默认二进制流
        };
    }
    /**
     * 提取首行作为文件名（过滤非法字符）
     * @param content 完整教案内容
     * @return 处理后的合法文件名
     */
    private String extractFileNameFromFirstLine(String content) {
        // 截取首行（以第一个换行符分割）
        String firstLine = content.split("\\r?\\n")[0];
        // 去掉首尾空格、换行符
        firstLine = firstLine.trim();
        // 过滤非法字符（替换为下划线）
        firstLine = Pattern.compile("[\\\\/:*?\"<>|]").matcher(firstLine).replaceAll("_");
        // 限制文件名长度（避免超过 COS 文件名限制，最大 255 字符）
        if (firstLine.length() > 50) {
            firstLine = firstLine.substring(0, 50) + "...";
        }
        // 兜底：若首行过滤后为空，用 UUID 兜底
        return StringUtils.hasText(firstLine) ? firstLine : "教案_" + System.currentTimeMillis();
    }
    @Override
    public PutObjectResultVO uploadFilePlus(MultipartFile multipartFile, String folder) {
        PutObjectResultVO putObjectResultVO = new PutObjectResultVO();
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/"+folder+"/%s", filename);
        File file = null;
        PutObjectResult putObjectResult=new PutObjectResult();
        putObjectResultVO.setFileUrl(cosClientConfig.getHost()+filepath);
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), filepath,
                    file);
            putObjectResult = cosClient.putObject(putObjectRequest);
            // 返回可访问地址

            putObjectResultVO.setPutObjectResult(putObjectResult);
            return putObjectResultVO;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);

        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
        return putObjectResultVO;

    }
}
