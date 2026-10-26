// RollCallService.java - 点名服务类
package com.zzx.exam.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RollCallService {
    
    /**
     * 解析花名册文件并返回学生列表
     * @param file 花名册文件
     * @return 学生列表
     */
    public List<String> parseRoster(MultipartFile file) throws Exception {
        List<String> students = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // 支持CSV格式，按逗号分割取第一个字段作为姓名
                    if (line.contains(",")) {
                        String[] parts = line.split(",");
                        if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                            students.add(parts[0].trim());
                        }
                    } else {
                        students.add(line);
                    }
                }
            }
        }
        
        return students;
    }
    
    /**
     * 随机点名
     * @param students 学生列表
     * @return 被点到的学生姓名
     */
    public String randomRollCall(List<String> students) {
        if (students == null || students.isEmpty()) {
            throw new IllegalArgumentException("学生列表不能为空");
        }
        
        int randomIndex = ThreadLocalRandom.current().nextInt(students.size());
        return students.get(randomIndex);
    }
    
    /**
     * 批量随机点名
     * @param students 学生列表
     * @param count 点名人数
     * @return 被点到的学生列表
     */
    public List<String> batchRollCall(List<String> students, int count) {
        if (students == null || students.isEmpty()) {
            throw new IllegalArgumentException("学生列表不能为空");
        }
        
        if (count <= 0) {
            throw new IllegalArgumentException("点名人数必须大于0");
        }
        
        // 如果点名人数大于等于学生总数，则返回所有学生
        if (count >= students.size()) {
            return new ArrayList<>(students);
        }
        
        // 创建副本避免修改原列表
        List<String> studentPool = new ArrayList<>(students);
        List<String> selectedStudents = new ArrayList<>();
        
        // 随机选取指定数量的学生
        for (int i = 0; i < count; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(studentPool.size());
            selectedStudents.add(studentPool.remove(randomIndex));
        }
        
        return selectedStudents;
    }
}