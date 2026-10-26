package com.zzx.exam.service.impl;

import com.zzx.exam.entity.ClassCourseRelation;
import com.zzx.exam.service.ClassCourseRelationService;
import com.zzx.exam.service.UserService;
import com.zzx.exam.vo.postgresql.AIChatVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.exam.entity.UserChatRelation;
import com.zzx.exam.service.UserChatRelationService;
import com.zzx.exam.mapper.UserChatRelationMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author zzx
* @description 针对表【user_chat_relation(用户会话关联表（一对多关系）)】的数据库操作Service实现
* @createDate 2025-12-01 18:33:14
*/
@DS("mysql")
@Service
public class UserChatRelationServiceImpl extends ServiceImpl<UserChatRelationMapper, UserChatRelation>
    implements UserChatRelationService {
    @Resource
    private UserService userService;
    @Resource
    private ClassCourseRelationService classCourseRelationService;
    @Override
    public List<String> listByUserId(Long userId) {
        QueryWrapper<UserChatRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<String> sessionIds = list(queryWrapper).stream().map(UserChatRelation::getSessionId).collect(Collectors.toList());
        return sessionIds;
    }

    @Override
    public Long beforeDoChat(AIChatVO aIChatVO, HttpServletRequest request) {
        QueryWrapper<ClassCourseRelation> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("class_id",aIChatVO.getClassId());
        queryWrapper3.eq("course_id",aIChatVO.getCourseId());
        Long courseId = classCourseRelationService.getOne(queryWrapper3).getCourseId();
        QueryWrapper<UserChatRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", aIChatVO.getConversationId());
        if(count(queryWrapper)==0L){
            Long userId = userService.getLoginUser(request).getId();
            UserChatRelation userChatRelation = new UserChatRelation();
            userChatRelation.setUserId(userId);
            userChatRelation.setSessionId(aIChatVO.getConversationId());
            save(userChatRelation);
        }
        return courseId;
    }
}




