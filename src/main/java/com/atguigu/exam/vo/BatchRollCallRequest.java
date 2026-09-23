package com.atguigu.exam.vo;

import lombok.Data;

import java.util.List;

/**
     * 批量点名请求DTO
     */
@Data
    public  class BatchRollCallRequest {
        private List<String> students;
        private int count;
        
        // Getters and setters
        public List<String> getStudents() {
            return students;
        }
        
        public void setStudents(List<String> students) {
            this.students = students;
        }
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
    }