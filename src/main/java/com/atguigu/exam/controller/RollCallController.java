// RollCallController.java - 点名控制器
package com.atguigu.exam.controller;


import com.atguigu.exam.common.Result;
import com.atguigu.exam.service.RollCallService;
import com.atguigu.exam.vo.BatchRollCallRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rollcall")
@Slf4j
public class RollCallController {
    
    @Autowired
    private RollCallService rollCallService;
    
    /**
     * 上传花名册文件
     * @param file 花名册文件
     * @return 学生列表
     */
    @PostMapping("/upload")
    public Result<List<String>> uploadRoster(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(400, "文件不能为空");
            }
            
            // 检查文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || 
                !(fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".csv") || fileName.endsWith(".txt"))) {
                return Result.error(400, "只支持.xls、.xlsx、.csv、.txt格式的文件");
            }
            
            List<String> students = rollCallService.parseRoster(file);
            return Result.success(students);
        } catch (Exception e) {
            log.error("上传花名册失败", e);
            return Result.error(500, "上传花名册失败：" + e.getMessage());
        }
    }
    
    /**
     * 随机点名
     * @param students 学生列表
     * @return 被点到的学生
     */
    @PostMapping("/random")
    public Result<String> randomRollCall(@RequestBody List<String> students) {
        try {
            String selectedStudent = rollCallService.randomRollCall(students);
            return Result.success(selectedStudent);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("随机点名失败", e);
            return Result.error(500, "随机点名失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量点名
     * @param students 学生列表
     * @param count 点名人数
     * @return 被点到的学生列表
     */
    @PostMapping("/batch")
    public Result<List<String>> batchRollCall(@RequestBody BatchRollCallRequest request) {
        try {
            List<String> selectedStudents = rollCallService.batchRollCall(request.getStudents(), request.getCount());
            return Result.success(selectedStudents);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量点名失败", e);
            return Result.error(500, "批量点名失败：" + e.getMessage());
        }
    }
    

}