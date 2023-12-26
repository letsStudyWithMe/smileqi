package com.smileqi.system.service;


import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.request.SysMenuQueryRequest;
import com.smileqi.user.model.domain.User;

import java.util.List;

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

    /**
     * 获取菜单展示结果
     * @param userId
     * @return
     */
    BaseResponse<List<SysMenu>> showSysMenu(Long userId);
}
