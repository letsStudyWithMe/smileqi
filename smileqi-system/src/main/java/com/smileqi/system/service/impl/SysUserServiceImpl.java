package com.smileqi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileqi.common.constant.CommonConstant;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.enums.UserRoleEnum;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.response.BaseResponse;
import com.smileqi.common.utils.JwtUtil;
import com.smileqi.common.utils.ResultUtils;
import com.smileqi.common.utils.SqlUtils;
import com.smileqi.system.mapper.SysUserMapper;
import com.smileqi.system.model.domain.SysUser;
import com.smileqi.system.model.request.SysUser.UserQueryRequest;
import com.smileqi.system.model.vo.LoginUserVO;
import com.smileqi.system.model.vo.UserVO;
import com.smileqi.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smileqi.common.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现
 *
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "smileqi";

    /**
     * 用户登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public BaseResponse<LoginUserVO> userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        SysUser user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 返回用户token
        String token = JwtUtil.createToken(user.getId());

        return ResultUtils.success(this.getLoginUserVO(user,token));
    }

    /**
     * 添加用户
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param userName 昵称
     * @param userRole 角色
     * @return
     */
    @Override
    public BaseResponse<Long> userRegister(String userAccount, String userPassword, String userName,String userRole) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword,userName,userRole)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            SysUser user = new SysUser();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userName);
            user.setUserRole(userRole);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return ResultUtils.success(user.getId());
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public SysUser getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        SysUser currentUser = (SysUser) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public SysUser getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        SysUser currentUser = (SysUser) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        SysUser user = (SysUser) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(SysUser user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public LoginUserVO getLoginUserVO(SysUser user,String token) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setToken(token);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(SysUser user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<SysUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<SysUser> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String userAccount = userQueryRequest.getUserAccount();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 修改密码
     * @param sysUser
     * @param passwordLater
     * @param password
     * @param passwordCheck
     */
    @Override
    public BaseResponse<Object> updatePassword(SysUser sysUser, String passwordLater, String password, String passwordCheck) {
        // 1.校验
        if (StringUtils.isAnyBlank(passwordLater,password,passwordCheck)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (password.length() < 8) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        String encryptPasswordLater = DigestUtils.md5DigestAsHex((SALT + passwordLater).getBytes());
        if (!sysUser.getUserPassword().equals(encryptPasswordLater)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "原始密码不正确");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 更新密码
        sysUser.setUserPassword(encryptPassword);
        boolean b = this.updateById(sysUser);
        return ResultUtils.success(b);
    }
}
