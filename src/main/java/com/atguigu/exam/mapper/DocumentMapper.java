package com.atguigu.exam.mapper;

import com.atguigu.exam.entity.Document;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author zzx
* @description 针对表【document(文档存储表)】的数据库操作Mapper
* @createDate 2025-11-27 21:31:39
* @Entity com.atguigu.exam.entity.Document
*/
public interface DocumentMapper extends BaseMapper<Document> {

    Integer countByMap(Map<String, Object> map);
}




