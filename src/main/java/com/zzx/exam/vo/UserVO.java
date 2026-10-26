package com.zzx.exam.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String realName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String role;



    /**
     * 创建时间
     */
    private Date createTime;



    private static final long serialVersionUID = 1L;
}
