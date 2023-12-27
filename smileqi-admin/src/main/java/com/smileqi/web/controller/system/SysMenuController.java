package com.smileqi.web.controller.system;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.exception.ThrowUtils;
import com.smileqi.common.request.DeleteRequest;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.common.utils.ResultUtils;
import com.smileqi.system.model.domain.SysMenu;
import com.smileqi.system.model.request.SysMenuAddRequest;
import com.smileqi.system.model.request.SysMenuQueryRequest;
import com.smileqi.system.model.request.SysMenuUpdateRequest;
import com.smileqi.system.service.SysMenuService;
import com.smileqi.user.model.domain.User;
import com.smileqi.user.model.vo.LoginUserVO;
import com.smileqi.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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
    @Resource
    private UserService userService;

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
        boolean result = sysMenuService.save(sysMenu);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(sysMenu.getId());
    }

    /**
     * 删除菜单
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSysMenu(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = sysMenuService.removeById(deleteRequest.getId());
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
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<SysMenu>> listSysMenuByPage(@RequestBody SysMenuQueryRequest sysMenuQueryRequest,
            HttpServletRequest request) {
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
        //登陆才可以使用
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return sysMenuService.showSysMenu(loginUser.getId());
    }
}
