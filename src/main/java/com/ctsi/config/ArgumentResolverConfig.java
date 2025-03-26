package com.ctsi.config;

import com.ctsi.system.service.IUserService;
import com.ctsi.system.support.MarsDSArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 11:40
 */
@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {


    @Autowired
    @Lazy
    private IUserService userService;

    /**
     * Token参数解析
     *
     * @param argumentResolvers 解析类
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //注入用户信息
        argumentResolvers.add(new MarsDSArgumentResolver(userService));
    }

}