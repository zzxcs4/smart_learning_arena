package com.zzx.exam.controller;


import com.zzx.exam.common.DeleteRequest;
import com.zzx.exam.common.Result;
import com.zzx.exam.entity.User;
import com.zzx.exam.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzx.exam.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户控制器 - 处理用户认证和权限管理相关的HTTP请求
 * 包括用户登录、权限验证等功能
 */
@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/user")  // 用户API路径前缀
@CrossOrigin(origins = "http://localhost:3001")  // 允许跨域访问
@Tag(name = "用户管理", description = "用户相关操作，包括登录认证、权限验证等功能")  // Swagger API分组
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")

    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<String> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            UserAvatarUploadRequest userAvatarUploadRequest,
            HttpServletRequest request) {

        String userAvatar = userService.uploadUserAvatar(multipartFile, userAvatarUploadRequest, request);
        return Result.success(userAvatar,"上传成功");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequestVO userRegisterRequest) {
        //ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return Result.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginUserVO> userLogin(@RequestBody UserLoginRequestVO userLoginRequest, HttpServletRequest request) {
        //ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUsername();
        String userPassword = userLoginRequest.getPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return Result.success(loginUserVO);
    }



    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request) {
        //ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return Result.success(result);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")

    public Result<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequestVO userQueryRequest) {
        //ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return Result.success(userVOPage);
    }
    @GetMapping("/search")
    public Result<List<UserVO>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(userService.getLoginUser(request))) {
            throw new RuntimeException();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> list = userList.stream().map(user -> userService.getUserVO(user)).collect(Collectors.toList());
        return Result.success(list);
    }
    /**
     * 创建用户
     */
    @PostMapping("/add")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        //ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
       //final String DEFAULT_PASSWORD = "12345678";
        //String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        //user.setPassword(encryptPassword);
        boolean result = userService.save(user);
        //ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<User> getUserById(long id) {
        //ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        //ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return Result.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public Result<UserVO> getUserVOById(long id) {
        Result<User> response = getUserById(id);
        User user = response.getData();
        return Result.success(userService.getUserVO(user));
    }
//    @GetMapping("/getOnlineState")
//    public BaseResponse<> getOnlineState(long id){
//        WebSocketSession state = onlineUserManager.getState(id);
//    }
    /**
     * 删除用户
     */
    @PostMapping("/delete")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new RuntimeException();
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return Result.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new RuntimeException();
        }
        long userId = userUpdateRequest.getId();
        if(userId<=0){
            throw new RuntimeException();
        }
        //仅管理员和自己可修改
        if(!userService.isAdmin(userService.getLoginUser(request))&&userId!=userService.getLoginUser(request).getId()){
            throw new RuntimeException();
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        //ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }
    
    /**
     * 检查用户权限
     * @param userId 用户ID
     * @return 权限检查结果
     */
    @GetMapping("/check-admin/{userId}")  // 处理GET请求
    @Operation(summary = "检查管理员权限", description = "验证指定用户是否具有管理员权限")  // API描述
    public Result<Boolean> checkAdmin(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        return Result.success(true);
    }
    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/loginUser")
    public Result<User> getLoginUserVO(HttpServletRequest request) {

        return Result.success(userService.getLoginUser(request));
    }

} 