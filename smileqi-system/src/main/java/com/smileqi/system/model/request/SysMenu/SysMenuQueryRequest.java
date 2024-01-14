package com.smileqi.system.model.request.SysMenu;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.smileqi.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜单查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuQueryRequest extends PageRequest implements Serializable {

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单状态（0正常 1停用）
     */
    private String status;

    private static final long serialVersionUID = 1L;
}