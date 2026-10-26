package com.zzx.exam.service.impl;


import com.zzx.exam.service.*;
import com.zzx.exam.springai.app.TeachingContentApp;
import com.zzx.exam.springai.rag.TeachingContentAppDocumentLoader;
import com.zzx.exam.utils.PlainTextToMarkdownUtil;
import com.zzx.exam.vo.AiGenerateTCRequestVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.exam.entity.Document;
import com.zzx.exam.mapper.DocumentMapper;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author zzx
* @description 针对表【document(文档存储表)】的数据库操作Service实现
* @createDate 2025-11-27 21:31:39
*/
@DS("mysql")
@Slf4j
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService {
        @Autowired
        private TeachingContentAppDocumentLoader teachingContentAppDocumentLoader;
        @Autowired
        private FileUploadService fileUploadService;
        @Autowired
        private UserService userService;
        @Autowired
        private RedisTemplate<String, Object> redisTemplate;
        @Autowired
        private VectorService vectorService;
        @Autowired
        private TeachingContentApp teachingContentApp;
        @Autowired
        private ClassCourseRelationService classCourseRelationService;
        @Autowired
        private FileUploadService fileService;

        @Override
        public void addVectorsByd(MultipartFile document , HttpServletRequest request,Long courseId,Integer status) {
            //上传文件到oss
            //1. 非空校验
            if (document.isEmpty()) {
                //配合全局异常处理，快速返回失败结果！！
                throw new RuntimeException("请选择要上传的文件！");
            }
            //2. 图片格式校验
            //获取文件的mimetype类型
//            String contentType = document.getContentType();
//            if (ObjectUtils.isEmpty(contentType) || !contentType.startsWith("image")) {
//                //配合全局异常处理，快速返回失败结果！！
//                throw new RuntimeException("轮播图只能上传图片文件！");
//            }
            //3. 文件大小限制
            if (document.getSize() > 10 * 1024 * 1024) {
                //配合全局异常处理，快速返回失败结果！！
                throw new RuntimeException("图片文件大小不能超过10MB");
            }

            //4. 调用文件上传业务
            String fileUrl = fileUploadService.uploadFile(document, "knowledge-base");
            //5. 返回结果
            log.info("完成资料上传，路径：{}",fileUrl);

            vectorService.extracted(document, fileUrl, courseId);
            Document document1 = new Document();
            document1.setFileName(document.getOriginalFilename());
            document1.setFileType(getFileSuffix(document.getOriginalFilename()));
            document1.setStoragePath(fileUrl);
            document1.setUploadUser(userService.getLoginUser(request).getId());
            document1.setCourseId(courseId);
            document1.setType(status);
            save(document1);
        }
    public static String getFileSuffix(String fileName) {
        // 1. 校验参数（避免空指针）
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // 2. 找到最后一个 "." 的索引
        int lastDotIndex = fileName.lastIndexOf('.');

        // 3. 处理无后缀的情况（如 "文件名" 或 ".gitignore"）
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        // 4. 截取后缀（从 "." 后面一位开始）
        return fileName.substring(lastDotIndex + 1);
    }





    /**
         * 根据文件名删除向量数据库中的所有相关片段
         * @param documentId 要删除的文件ID
         * @return 删除是否成功
         */
        @Override
        public void deleteVectorsByFileName(Long documentId) {
            Document document = getById(documentId);
            vectorService.deleteVectors(document);
            //删除数据库中数据
            removeById(documentId);
        }

    @Override
    public String aiGenerateTC(AiGenerateTCRequestVO aiGenerateTCRequestVO) {
            //根据参数调用ai即可
        String content = teachingContentApp.doChatWithRag(
                "生成教案，主题为：\n" + aiGenerateTCRequestVO.getTopic() + "\n其他要求" + aiGenerateTCRequestVO.getOther()+"\n注意：生成的教案首行用\""+aiGenerateTCRequestVO.getTopic()+"教案\"作为标题"
                , aiGenerateTCRequestVO.getTemplate(), aiGenerateTCRequestVO.getCourseId());

        return PlainTextToMarkdownUtil.convertToMarkdown(content);
    }

    @Override
    public String importTc(String teachingContent, Long courseId, HttpServletRequest request) {

        if (StringUtil.isBlank(teachingContent)) {
            throw new RuntimeException("上传内容不能为空");
        }
        Long userId = userService.getLoginUser(request).getId();
        // 默认文件后缀为 .txt（若未指定）
//        if (!StringUtils.hasText(fileSuffix)) {
//            fileSuffix = ".txt";
//        }
//        if (!fileSuffix.startsWith(".")) {
//            fileSuffix = "." + fileSuffix;
//        }
        String fileUrl = fileService.uploadFileByContent(teachingContent, userId);
        //存入数据库
        Document document = new Document();
        document.setFileName(removeFileSuffix(extractFullFileNameFromCosPath(fileUrl)));
        document.setFileType(getFileSuffix(fileUrl));
        document.setStoragePath(fileUrl);
        document.setUploadUser(userId);
        document.setCourseId(courseId);
        document.setType(1);
        save(document);
        // 1. 生成唯一文件名（避免重复）：UUID + 日期 + 后缀
        return fileUrl;
    }

    // 引入提取工具方法（直接在 TencentCosUtil 中添加）
    private String extractFullFileNameFromCosPath(String cosFilePath) {
        if (cosFilePath == null || cosFilePath.isEmpty()) {
            return "";
        }
        return cosFilePath.substring(cosFilePath.lastIndexOf("/") + 1);
    }

    private String removeFileSuffix(String fullFileName) {
        if (fullFileName == null || fullFileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fullFileName.lastIndexOf(".");
        return lastDotIndex != -1 ? fullFileName.substring(0, lastDotIndex) : fullFileName;
    }
    /**
         * 批量删除多个文件
         */
        public void deleteVectorsByFileNames(List<Long> fileNames) {

            for (Long fileName : fileNames) {
                deleteVectorsByFileName(fileName);

            }

        }

        /**
         * 使用 IN 操作符批量删除（如果支持）
         */
//        public void batchDeleteVectors(List<Long> fileNames) {
//            try {
//                // 构建 IN 表达式：source IN ['file1', 'file2', ...]
//                Filter.Expression filterExpression = new Filter.Expression(
//                        Filter.ExpressionType.IN,
//                        new Filter.Key("source"),
//                        new Filter.Value(fileNames.toArray(new String[0]))
//                );
//
//                vectorStore.delete(filterExpression);
//
//
//            } catch (Exception e) {
//                System.err.println("批量删除失败，回退到单个删除: " + e.getMessage());
//                deleteVectorsByFileNames(fileNames);
//            }
//        }


}




