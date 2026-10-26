package com.zzx.exam.service.postgresql.impl;

import cn.hutool.core.util.ObjUtil;
import com.zzx.exam.entity.postgresql.AiChatMemory;
import com.zzx.exam.mapper.postgresql.AiChatMemoryMapper;
import com.zzx.exam.service.postgresql.IAiChatMemoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * AI对话记忆表 服务实现类
 * </p>
 */
@Slf4j
@Service
public class AiChatMemoryServiceImpl extends ServiceImpl<AiChatMemoryMapper, AiChatMemory> implements IAiChatMemoryService {

    @Override
    public List<AiChatMemory> getConversationList(List<String> sessionIds) {


        List<AiChatMemory> list = new ArrayList<>();
        for(String sessionId:sessionIds){
            QueryWrapper<AiChatMemory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ObjUtil.isNotEmpty(sessionIds),"conversation_id", sessionId);
            queryWrapper.eq("type","USER");
            queryWrapper.orderByAsc("timestamp");
            queryWrapper.last("OFFSET 0 LIMIT 1");
            AiChatMemory memory = getOne(queryWrapper);
            list.add(memory);
        }
        return list;
    }

    @Override
    public List<AiChatMemory> getConversationHistory(String conversationId) {
        return this.baseMapper.selectByConversationId(conversationId);
    }

    @Override
    public boolean saveMessage(String conversationId, String content, AiChatMemory.MessageType type) {
        try {
            AiChatMemory memory = new AiChatMemory()
                    .setConversationId(conversationId)
                    .setContent(content)
                    .setType(type)
                    .setTimestamp(LocalDateTime.now());
            
            return this.save(memory);
        } catch (Exception e) {
            log.error("保存对话记录失败, conversationId: {}, type: {}", conversationId, type, e);
            return false;
        }
    }

    @Override
    public boolean deleteConversation(String conversationId) {
        try {
            int result = this.baseMapper.deleteByConversationId(conversationId);
            log.info("删除会话记录成功, conversationId: {}, 删除条数: {}", conversationId, result);
            return result > 0;
        } catch (Exception e) {
            log.error("删除会话记录失败, conversationId: {}", conversationId, e);
            return false;
        }
    }

    @Override
    public List<String> getRecentConversations(int limit) {
        return this.baseMapper.selectRecentConversations(limit);
    }
}