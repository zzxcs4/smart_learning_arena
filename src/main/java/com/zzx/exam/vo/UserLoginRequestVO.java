package com.zzx.exam.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequestVO implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
