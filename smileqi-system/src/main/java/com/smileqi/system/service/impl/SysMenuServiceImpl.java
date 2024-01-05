package com.smileqi.system.service.impl;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.enums.UserRoleEnum;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.model.MenuItem;
import com.smileqi.common.model.Meta;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.common.utils.ResultUtils;
import com.smileqi.system.mapper.SysMenuMapper;
import com.smileqi.system.mapper.SysUserMapper;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.domain.SysUser;
import com.smileqi.system.model.request.SysMenu.SysMenuQueryRequest;
import com.smileqi.system.service.SysMenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.swing.plaf.metal.MetalBorders;
import java.util.*;

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
    @Resource
    private SysUserMapper userMapper;


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
     * @param userId
     * @return
     */
    @Override
    public BaseResponse<JSONArray> showSysMenu(Long userId) {
        QueryWrapper<SysUser> queryWrapperUser = new QueryWrapper<>();
        queryWrapperUser.eq("id",userId);
        SysUser loginUser = userMapper.selectOne(queryWrapperUser);
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

/*        List<Map<String, Object>> menuList = new ArrayList<>();

        Map<String, Object> menu1 = new HashMap<>();
        menu1.put("path", "/dashboard");
        menu1.put("name", "dashboard");
        Map<String, Object> meta1 = new HashMap<>();
        meta1.put("locale", "menu.server.dashboard");
        meta1.put("requiresAuth", true);
        meta1.put("icon", "icon-dashboard");
        meta1.put("order", 1);
        menu1.put("meta", meta1);

        List<Map<String, Object>> children1 = new ArrayList<>();
        Map<String, Object> child1 = new HashMap<>();
        child1.put("path", "workplace");
        child1.put("name", "Workplace");
        Map<String, Object> meta2 = new HashMap<>();
        meta2.put("locale", "menu.server.workplace");
        meta2.put("requiresAuth", true);
        child1.put("meta", meta2);
        children1.add(child1);

        Map<String, Object> child2 = new HashMap<>();
        child2.put("path", "https://arco.design");
        child2.put("name", "arcoWebsite");
        Map<String, Object> meta3 = new HashMap<>();
        meta3.put("locale", "menu.arcoWebsite");
        meta3.put("requiresAuth", true);
        child2.put("meta", meta3);
        children1.add(child2);

        menu1.put("children", children1);
        menuList.add(menu1);

        System.out.println(menuList);

        JSONArray objects = new JSONArray(menuList);*/

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


/*        for (SysMenu menu : res) {
            MenuItem menuItem = new MenuItem();
            menuItem.setPath(menu.getPath());
            menuItem.setName(menu.getName());
            menuItem.setMeta(new Meta(menu.getLocale(),
                    menu.isRequiresAuth(),
                    menu.getIcon(),
                    menu.getOrderNum()));
            List<MenuItem> resultChild = new ArrayList<>();
            for (SysMenu child : menu.getChildren()) {
                MenuItem menuItemChildren = new MenuItem();
                menuItemChildren.setPath(child.getPath());
                menuItemChildren.setName(child.getName());
                menuItemChildren.setMeta(new Meta(child.getLocale(),
                        child.isRequiresAuth(),
                        child.getIcon(),
                        child.getOrderNum()));
                resultChild.add(menuItemChildren);
            }
            menuItem.setChildren(resultChild);
        }*/
        List<MenuItem> result = new ArrayList<>();

        int index = 0;
        while (index < res.size()) {
            SysMenu menu = res.get(index);
            MenuItem menuItem = new MenuItem();
            menuItem.setPath(menu.getPath());
            menuItem.setName(menu.getName());
            menuItem.setMeta(new Meta(menu.getLocale(),
                    menu.isRequiresAuth(),
                    menu.getIcon(),
                    menu.getOrderNum()));
            List<MenuItem> resultChild = new ArrayList<>();
            int childIndex = 0;
            while (childIndex < menu.getChildren().size()) {
                SysMenu child = menu.getChildren().get(childIndex);
                MenuItem menuItemChildren = new MenuItem();
                menuItemChildren.setPath(child.getPath());
                menuItemChildren.setName(child.getName());
                menuItemChildren.setMeta(new Meta(child.getLocale(),
                        child.isRequiresAuth(),
                        child.getIcon(),
                        child.getOrderNum()));
                resultChild.add(menuItemChildren);
                childIndex++;
            }
            menuItem.setChildren(resultChild);
            index++;
            result.add(menuItem);
        }

       /* //组成菜单树
        Iterator<MenuItem> iter = result.iterator();
        try {
            while (iter.hasNext()) {
                MenuItem menuItem = iter.next();
                Long sysMenuId = menuItem.getId();
                for (MenuItem menuItemChildren : result) {
                    Long parentId = menuItemChildren.getParentId();
                    if (sysMenuId.equals(parentId) && menuItemChildren != null) {
                        menuItem.setChildren(menuItemChildren);
                    }
                }
                if (menuItem.getParentId() != 0) {
                    iter.remove();
                }
            }
        } catch (Exception e) {
            log.info("菜单树组装异常"+e.toString());
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"菜单树组装异常");
        }*/
        JSONArray resultFinal = new JSONArray(result);
        System.out.println(resultFinal);
        return ResultUtils.success(resultFinal);
    }
}




