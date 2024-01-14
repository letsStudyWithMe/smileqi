package com.smileqi.system.model.request.SysMenu;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单创建请求
 */
@Data
public class SysMenuAddRequest implements Serializable {

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单Id
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    /**
     * 国际化
     */
    private String locale;

    /**
     *  是否需要访问权限
     */
    private boolean requiresAuth;

    /**
     *  状态
     */
    private String status;



    private static final long serialVersionUID = 1L;
}