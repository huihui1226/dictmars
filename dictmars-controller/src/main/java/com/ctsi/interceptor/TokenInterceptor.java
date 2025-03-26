package com.ctsi.interceptor;

import com.ctsi.core.common.constant.Oauth2Constant;
import com.ctsi.core.common.util.SecurityUtil;
import com.ctsi.core.common.util.StringPool;
import com.ctsi.core.common.util.TokenUtil;
import com.ctsi.core.redis.core.RedisService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/4 10:07
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private static final String[] ENDPOINTS = {
            "/oauth/**",
            "/v2/api-docs/**",
            "/swagger/api-docs",
            "/swagger-ui.html",
            "/doc.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/error/**",
            "/assets/**",
            "/auth/logout",
            "/auth/code"
    };

    @Autowired
    RedisService redisService;

    /**
     * 路径前缀以/mars开头，如dictmars-system
     */
    public static final String PATH_PREFIX = "/mars";

    /**
     * 索引自1开头检索，跳过第一个字符就是检索的字符的问题
     */
    public static final int FROM_INDEX = 1;

    PrintWriter writer;

    /**
     * 检查是否忽略url
     * @param path 路径
     * @return boolean
     */
    private boolean ignore(String path) {
        List<String> ignoreUrl = new ArrayList<>();
        Collections.addAll(ignoreUrl, ENDPOINTS);
        for (String url : ignoreUrl) {
            if (url.contains(path)) {
                return true;
            }
            if (url.contains("/**")) {
                if (url.substring(0, url.indexOf("/**")).contains(path.substring(0, path.lastIndexOf("/")))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 移除模块前缀
     * @param path 路径
     * @return String
     */
    private String replacePrefix(String path) {
        if (path.startsWith(PATH_PREFIX)) {
            return path.substring(path.indexOf(StringPool.SLASH, FROM_INDEX));
        }
        return path;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //　如果在忽略的url里，则跳过
        String path = replacePrefix(request.getRequestURI());
        //swagger
        if (path.startsWith("/webjars")) {
            return true;
        }
        if (ignore(path)) {
           return true;
        }
        // 验证token是否有效
        String headerToken = request.getHeader(Oauth2Constant.HEADER_TOKEN);
        if (headerToken == null) {
            writer = response.getWriter();
            writer.print("没有携带Token信息！");
            return false;
        }
        String token = TokenUtil.getToken(headerToken);
        Claims claims = SecurityUtil.getClaims(token);
        if (claims == null) {
            writer = response.getWriter();
            writer.print("token已过期或验证不正确！");
            return false;
        }
        // 判断token是否存在于redis,对于只允许一台设备场景适用。
        // 如只允许一台设备登录，需要在登录成功后，查询key是否存在，如存在，则删除此key，提供思路。
        boolean hasKey = redisService.hasKey("auth:" + token);
        log.debug("查询token是否存在: " + hasKey);
        if (!hasKey) {
            writer = response.getWriter();
            writer.print("登录超时，请重新登录");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {
    }
}
