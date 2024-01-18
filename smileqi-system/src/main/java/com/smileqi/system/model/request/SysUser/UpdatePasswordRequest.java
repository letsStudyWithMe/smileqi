package com.smileqi.system.model.request.SysUser;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data
public class UpdatePasswordRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userPasswordLater;

    private String userPassword;

    private String userPasswordCheck;
}
