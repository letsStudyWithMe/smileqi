package com.smileqi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.system.mapper.SysMenuMapper;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.request.SysMenuQueryRequest;
import com.smileqi.system.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

/**
* @author smileqi
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2023-12-21 10:33:06
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService {

    /**
     * 根据查询条件进行查询
     *
     * @param sysMenuQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SysMenu> getQueryWrapper(SysMenuQueryRequest sysMenuQueryRequest) {
        if (sysMenuQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String name = sysMenuQueryRequest.getName();
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        return queryWrapper;
    }
}




