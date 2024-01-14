package com.smileqi.web.controller.system;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.exception.ThrowUtils;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.common.utils.JwtUtil;
import com.smileqi.common.utils.ResultUtils;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.request.SysMenu.SysMenuAddRequest;
import com.smileqi.system.model.request.SysMenu.SysMenuQueryRequest;
import com.smileqi.system.model.request.SysMenu.SysMenuUpdateRequest;
import com.smileqi.system.service.SysMenuService;
import com.smileqi.system.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * 菜单接口
 *
 */
@RestController
@RequestMapping("/sysmenu")
@Slf4j
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 创建菜单
     *
     * @param sysMenuAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSysMenu(@RequestBody SysMenuAddRequest sysMenuAddRequest, HttpServletRequest request) {
        if (sysMenuAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(sysMenuAddRequest, sysMenu);
        sysMenu.setCreateTime(new Date());
        boolean result = sysMenuService.save(sysMenu);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(sysMenu.getId());
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteSysMenu(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(id);
        sysMenu.setStatus("1");
        boolean b = sysMenuService.updateById(sysMenu);
        return ResultUtils.success(b);
    }

    /**
     * 更新菜单
     *
     * @param sysMenuUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateSysMenu(@RequestBody SysMenuUpdateRequest sysMenuUpdateRequest,
            HttpServletRequest request) {
        if (sysMenuUpdateRequest == null || sysMenuUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(sysMenuUpdateRequest, sysMenu);
        boolean result = sysMenuService.updateById(sysMenu);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取菜单列表（仅管理员）
     *
     * @param sysMenuQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<SysMenu>> listSysMenuByPage(@RequestBody SysMenuQueryRequest sysMenuQueryRequest) {
        long current = sysMenuQueryRequest.getCurrent();
        long size = sysMenuQueryRequest.getPageSize();
        Page<SysMenu> sysMenuPage = sysMenuService.page(new Page<>(current, size),
                sysMenuService.getQueryWrapper(sysMenuQueryRequest));
        return ResultUtils.success(sysMenuPage);
    }

    /**
     * 获取菜单展示结果
     *
     * @param request
     * @return
     */
    @GetMapping("/showSysMenu")
    public BaseResponse<JSONArray> showSysMenu(HttpServletRequest request) {
        Long loginUserId = JwtUtil.getLoginUserId(request);
        return sysMenuService.showSysMenu(loginUserId);
    }

    /**
     * 分页获取菜单列表（仅管理员）
     *
     * @param sysMenuQueryRequest
     * @return
     */
    @PostMapping("/list/page/menuTree")
    public BaseResponse<Page<SysMenu>> listSysMenuByPageMenuTree(@RequestBody SysMenuQueryRequest sysMenuQueryRequest) {
        long current = sysMenuQueryRequest.getCurrent();
        long size = sysMenuQueryRequest.getPageSize();
        Page<SysMenu> sysMenuPage = sysMenuService.page(new Page<>(current, size),
                sysMenuService.getQueryWrapper(sysMenuQueryRequest));
        if (sysMenuPage.getTotal() <= 1 || "1".equals(sysMenuQueryRequest.getStatus())){
            return ResultUtils.success(sysMenuPage);
        }
        //组成菜单树
        Iterator<SysMenu> iter = sysMenuPage.getRecords().iterator();
        try {
            while (iter.hasNext()) {
                SysMenu sysMenu = iter.next();
                Long sysMenuId = sysMenu.getId();
                for (SysMenu sysMenuChildren : sysMenuPage.getRecords()) {
                    Long parentId = sysMenuChildren.getParentId();
                    if (sysMenuId.equals(parentId) && sysMenuChildren != null) {
                        sysMenu.setChildren(sysMenuChildren);
                    }
                }
                if (sysMenu.getParentId() != 0){
                    iter.remove();
                }
            }
        } catch (Exception e) {
            log.info("菜单树组装异常"+e.toString());
        }
        sysMenuPage.setTotal(sysMenuPage.getRecords().size());
        return ResultUtils.success(sysMenuPage);
    }

    /**
     * 根据 id 获取菜单
     *
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public BaseResponse<SysMenu> getMenuById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysMenu sysMenu = sysMenuService.getById(id);
        ThrowUtils.throwIf(sysMenu == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(sysMenu);
    }
}
