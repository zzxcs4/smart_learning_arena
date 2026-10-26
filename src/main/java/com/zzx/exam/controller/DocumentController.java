package com.zzx.exam.controller;

import cn.hutool.core.util.ObjUtil;
import com.zzx.exam.common.Result;
import com.zzx.exam.entity.Document;
import com.zzx.exam.service.ClassCourseRelationService;
import com.zzx.exam.service.DocumentService;
import com.zzx.exam.service.FileUploadService;
import com.zzx.exam.springai.app.TeachingContentApp;
import com.zzx.exam.utils.PlainTextToMarkdownUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzx.exam.vo.AiGenerateTCRequestVO;
import com.zzx.exam.vo.ImportRequest;
import com.zzx.exam.vo.PutObjectResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@Tag(name = "文档管理", description = "文档相关操作，包括文档的增删改查")
public class DocumentController {
    @Autowired
    private DocumentService documentVectorService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private ClassCourseRelationService classCourseRelationService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private TeachingContentApp teachingContentApp;
    /**
     * 添加知识库文档
     */
    @PostMapping("/add")
    @Operation(summary = "添加知识库文档(老师)", description = "添加知识库文档")  // API描述
    public void addVectorsByd(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request,Long courseId,Integer type) {

        documentVectorService.addVectorsByd(multipartFile,request,courseId,type);

    }
    @GetMapping("/templates")
    public Result<Map<Integer,String>> getTemplate(){
        Map<Integer,String> templates = new HashMap<Integer,String>();

        templates.put(1, PlainTextToMarkdownUtil.convertToMarkdown(teachingContentApp.getTemplate1()) );
        templates.put(2,PlainTextToMarkdownUtil.convertToMarkdown(teachingContentApp.getTemplate2())) ;
        return Result.success(templates);
    }
    @GetMapping("/list")
    public Result<List<Document>> listDocuments(HttpServletRequest request,Long courseId,String name,Integer type) {

        QueryWrapper<Document> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(ObjUtil.isNotEmpty(type),"type",type);
        queryWrapper1.eq(ObjUtil.isNotEmpty(courseId),"course_id",courseId);
        queryWrapper1.eq(StringUtil.isNotBlank(name),"file_name",name);
        List<Document> list = documentVectorService.list(queryWrapper1);
        return Result.success(list);

    }


    /**
     * 添加知识库文档
     */
    @PostMapping("/addd")
    @Operation(summary = "添加知识库文档(老师)", description = "添加知识库文档")  // API描述
    public void adddByd(@RequestPart("file") MultipartFile multipartFile) {
//上传文件到oss
        //1. 非空校验
        if (multipartFile.isEmpty()) {
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
        if (multipartFile.getSize() > 10 * 1024 * 1024) {
            //配合全局异常处理，快速返回失败结果！！
            throw new RuntimeException("图片文件大小不能超过10MB");
        }
        //4. 调用文件上传业务
        PutObjectResultVO putObjectResult = fileUploadService.uploadFilePlus(multipartFile, "knowledge-base");
        //5. 返回结果
        log.info("完成资料上传，数据：{}",putObjectResult);



    }
    /**
     * 删除文档及其向量数据
     */
    @DeleteMapping("/{documentId}")
    @Operation(summary = "删除知识库文档(老师)", description = "删除知识库文档")  // API描述
    public void deleteDocument(@PathVariable Long documentId) {

        documentVectorService.deleteVectorsByFileName(documentId);
        //数据库删除对应数据


    }
    /**
     * AI对接知识库生成教案
     */
    @PostMapping("/AiGTeachingContent")
    public Result<String> AiGTeachingContent(@RequestBody AiGenerateTCRequestVO aiGenerateTCRequestVO, HttpServletRequest request) {
        return Result.success(documentVectorService.aiGenerateTC(aiGenerateTCRequestVO),"返回成功") ;
    }
    @PostMapping("/importTc")
    public Result<String> importQuestions(@RequestBody ImportRequest importRequest, HttpServletRequest request) {
        String successCount =  documentVectorService.importTc(importRequest.getTeachingContent(),importRequest.getCourseId(),request);
        //log.info("导入接口接口调用成功！ 一共：{}题目需要导入，成功导入了：{}道题！" ,batchGenerateVO.getQuestionImportVoList().size(),successCount);
        return Result.success(successCount,"导入教案接口调用成功！ 可直接下载"+successCount);

    }
    /**
     * 批量删除文档
     */
//    @PostMapping("/batch-delete")
//    public void  batchDeleteDocuments(@RequestBody List<String> fileNames) {
//
//            documentVectorService.batchDeleteVectors(fileNames);
//
//
//
//
//    }
}