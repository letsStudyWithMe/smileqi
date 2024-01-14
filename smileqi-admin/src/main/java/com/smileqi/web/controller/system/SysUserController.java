package com.smileqi.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileqi.common.annotation.PassToken;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.exception.ThrowUtils;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.common.utils.JwtUtil;
import com.smileqi.common.utils.ResultUtils;
import com.smileqi.system.model.domain.SysUser;
import com.smileqi.system.model.request.SysUser.*;
import com.smileqi.system.model.vo.LoginUserVO;
import com.smileqi.system.model.vo.UserVO;
import com.smileqi.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/sysuser")
@Slf4j
public class SysUserController {

    @Resource
    private SysUserService userService;

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @PassToken
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        return userService.userLogin(userAccount, userPassword, request);

    }

    /**
     * 添加用户
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "添加用户")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String userName = userRegisterRequest.getUserName();
        String userRole = "user";
        if (!StringUtils.isAnyBlank(userRegisterRequest.getUserRole())){
            userRole = userRegisterRequest.getUserRole();
        }
        return userService.userRegister(userAccount, userPassword,userName,userRole);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getLoginUserId(request);
        SysUser sysUser = userService.getById(userId);
        return ResultUtils.success(userService.getLoginUserVO(sysUser,token));
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public BaseResponse<SysUser> getUserById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteUser(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setIsDelete(1);
        boolean b = userService.updateById(sysUser);
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<SysUser> response = getUserById(id);
        SysUser user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<SysUser>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<SysUser> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<SysUser> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser loginUser = userService.getLoginUser(request);
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
