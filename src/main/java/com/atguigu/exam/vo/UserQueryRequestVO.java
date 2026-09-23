package com.atguigu.exam.vo;

import com.atguigu.exam.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequestVO extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String realName;

    /**
     * 账号
     */
    private String username;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String role;

    private static final long serialVersionUID = 1L;
}
