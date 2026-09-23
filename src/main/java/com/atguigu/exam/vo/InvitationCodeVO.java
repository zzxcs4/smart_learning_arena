package com.atguigu.exam.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvitationCodeVO  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String invitationCode;
}
