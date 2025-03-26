package com.ctsi.system.support;

import com.ctsi.core.common.entity.LoginUser;
import com.ctsi.core.common.entity.MarsDS;
import com.ctsi.core.common.util.SecurityUtil;
import com.ctsi.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/9 16:13
 */
@Slf4j
public class MarsDSArgumentResolver implements HandlerMethodArgumentResolver {

    private final IUserService userService;

    public MarsDSArgumentResolver(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(MarsDS.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        LoginUser loginUser = SecurityUtil.getUsername(nativeWebRequest.getNativeRequest(HttpServletRequest.class));
        MarsDS marsDS = new MarsDS();
        marsDS.setUserId(loginUser.getId());
        // 获取数据范围数据
//        marsDS.setOrgIds(userService.getDataScope(loginUser.getId()));
        return marsDS;
    }
}
