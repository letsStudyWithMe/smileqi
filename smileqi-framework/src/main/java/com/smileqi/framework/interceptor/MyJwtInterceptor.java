package com.smileqi.framework.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import com.smileqi.common.annotation.PassToken;
import com.smileqi.common.enums.ErrorCode;
import com.smileqi.common.exception.BusinessException;
import com.smileqi.common.utils.JwtUtil;
import com.smileqi.system.model.domain.SysUser;
import com.smileqi.system.service.SysUserService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class MyJwtInterceptor implements HandlerInterceptor {
    /**
     * @Field: KEY  加密的秘钥
     */
    private final static String KEY="smileqi";
    @Resource
    private SysUserService sysUserService;
 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //检查是否通过有PassToken注解
        if (method.isAnnotationPresent(PassToken.class)) {
            //如果有则跳过认证检查
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //否则进行token检查
        if (CharSequenceUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NO_TOKEN);
        }
        //获取token中的用户id
        Long userId;
        try {
            Claims claims = JwtUtil.parseToken(token);
            userId = (Long) claims.get("userId");
        } catch (Exception e) {
            System.out.println(e);
            throw new BusinessException(ErrorCode.TOKEN_ERROR);
        }
        //根据token中的userId查询数据库
        SysUser sysUser = sysUserService.getById(userId);
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return true;
    }
}
