package com.smileqi.system.service.impl;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.enums.UserRoleEnum;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.common.utils.ResultUtils;
import com.smileqi.system.mapper.SysMenuMapper;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.request.SysMenuQueryRequest;
import com.smileqi.system.service.SysMenuService;
import com.smileqi.user.model.domain.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* @author smileqi
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2023-12-21 10:33:06
*/
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

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

    /**
     * 获取菜单展示结果
     * @param loginUser
     * @return
     */
    @Override
    public BaseResponse<List<SysMenu>> showSysMenu(User loginUser) {
        //查询菜单列表
        List<SysMenu> res = new ArrayList<>();
        if (loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())){
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status",0)
                    .orderBy(true, true, "parentId", "orderNum")
                    .select(SysMenu.class,i->!i.getProperty().equals("children")); ;
            res = sysMenuMapper.selectList(queryWrapper);
        }else {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status",0)
                    .like("perms",loginUser.getUserRole())
                    .orderBy(true, true, "parentId", "orderNum")
                    .select(SysMenu.class,i->!i.getProperty().equals("children")); ;
            res = sysMenuMapper.selectList(queryWrapper);
        }

        //组成菜单树
        Iterator<SysMenu> iter = res.iterator();
        try {
            while (iter.hasNext()) {
                SysMenu sysMenu = iter.next();
                Long sysMenuId = sysMenu.getId();
                for (SysMenu sysMenuChildren : res) {
                    Long parentId = sysMenuChildren.getParentId();
                    if (sysMenuId.equals(parentId) && sysMenuChildren != null) {
                        sysMenu.setChildren(sysMenuChildren);
                    }
                }
                if (sysMenu.getParentId() != 0) {
                    iter.remove();
                }
            }
        } catch (Exception e) {
            log.info("菜单树组装异常"+e.toString());
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"菜单树组装异常");
        }
        return ResultUtils.success(res);
    }
}




