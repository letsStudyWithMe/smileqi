package com.smileqi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.system.model.domain.SysUser;
import com.smileqi.system.model.request.SysUser.UserQueryRequest;
import com.smileqi.system.model.vo.LoginUserVO;
import com.smileqi.system.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户服务
 *
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 添加用户
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param userName 昵称
     * @return 新用户 id
     */
    BaseResponse<Long> userRegister(String userAccount, String userPassword, String userName, String userRole);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    BaseResponse<LoginUserVO> userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    SysUser getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    SysUser getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(SysUser user);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(SysUser user,String token);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(SysUser user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<SysUser> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<SysUser> getQueryWrapper(UserQueryRequest userQueryRequest);
}
