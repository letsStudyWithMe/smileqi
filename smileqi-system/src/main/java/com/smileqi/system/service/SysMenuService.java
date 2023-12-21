package com.smileqi.system.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.request.SysMenuQueryRequest;

/**
* @author smileqi
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service
* @createDate 2023-12-21 10:33:06
*/
public interface SysMenuService extends IService<SysMenu> {
    /**
     * 根据查询条件进行查询
     *
     * @param sysMenuQueryRequest
     * @return
     */
    QueryWrapper<SysMenu> getQueryWrapper(SysMenuQueryRequest sysMenuQueryRequest);
}
