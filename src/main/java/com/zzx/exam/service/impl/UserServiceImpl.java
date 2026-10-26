package com.zzx.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.zzx.exam.common.UserRoleEnum;
import com.zzx.exam.entity.User;
import com.zzx.exam.mapper.UserMapper;
import com.zzx.exam.service.FileUploadService;
import com.zzx.exam.service.UserService;
import com.atguigu.exam.vo.*;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.zzx.exam.vo.LoginUserVO;
import com.zzx.exam.vo.UserAvatarUploadRequest;
import com.zzx.exam.vo.UserQueryRequestVO;
import com.zzx.exam.vo.UserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service实现类
 * 实现用户相关的业务逻辑
 */
@DS("mysql")
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private FileUploadService fileUploadService;
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute("user_login");
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new RuntimeException();
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new RuntimeException();
        }
        return currentUser;
    }
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new RuntimeException();
        }
        if (userAccount.length() < 4) {
            throw new RuntimeException();
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new RuntimeException();
        }
        if (!userPassword.equals(checkPassword)) {
            throw new RuntimeException();
        }
        // 2. 检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException();
        }
        // 3. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = new User();
        user.setUsername(userAccount);
        user.setPassword(encryptPassword);
        user.setRealName("无名");
        user.setRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new RuntimeException( "注册失败，数据库错误");
        }
        return user.getId();
    }
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "yupi";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }


    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new RuntimeException();
        }
        if (userAccount.length() < 4) {
            throw new RuntimeException();
        }
        if (userPassword.length() < 4) {
            throw new RuntimeException();
        }
        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount);
        //queryWrapper.eq("password", encryptPassword);
        queryWrapper.eq("password", userPassword);
        User user = getOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new RuntimeException();
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute("user_login", user);

        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }


    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute("user_login");
        if (userObj == null) {
            throw new RuntimeException("未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute("user_login");
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequestVO userQueryRequest) {
        if (userQueryRequest == null) {
            throw new RuntimeException( "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUsername();
        String userName = userQueryRequest.getRealName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "role", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "username", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "real_Name", userName);
        //queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getRole());
    }

    @Override
    public String uploadUserAvatar(MultipartFile multipartFile, UserAvatarUploadRequest userAvatarUploadRequest, HttpServletRequest request) {
        if (multipartFile == null) {
            throw new RuntimeException("图片为空");
        }
        User loginUser = this.getLoginUser(request);
        if(loginUser == null){
            throw new RuntimeException("用户不存在");
        }
        // 用于判断是新增还是更新图片
        String userAvatar= loginUser.getUserAvatar();

        // 如果是更新图片，需要校验图片是否存在
        if (userAvatar != null) {
            String oldUserAvatar = userAvatar;
            if (oldUserAvatar == null) {
                throw new RuntimeException("图片不存在");
            }
        }
        // 仅本人或管理员可编辑
//            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//            }


        // 上传图片，得到信息




        // 按照用户 id 划分目录 => 按照空间划分目录
        String uploadPathPrefix;

        uploadPathPrefix = String.format("userAvatar/%s", loginUser.getId());




        String url = fileUploadService.uploadFile(multipartFile, uploadPathPrefix);
            // 构造要入库的图片信息

        loginUser.setUserAvatar(url);


        boolean result = this.updateById(loginUser);
        if(!result){
            throw new RuntimeException("图片上传失败");
        }




        return url;
    }


}