package com.atguigu.exam.vo.postgresql;

import lombok.Data;

@Data
public class AIChatVO {
   private String conversationId ;
   private Long courseId;
   private Long classId;
   private String query;
   private Boolean webSearch;
}
