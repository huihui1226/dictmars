package com.ctsi.uaa.app;

import com.ctsi.core.security.userdetails.MarsUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:50
 */
@Slf4j
@Component
public class AppAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private MarsUserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) {
         //获取验证码提供者
        AppAuthenticationProvider appAuthenticationProvider = new AppAuthenticationProvider(userDetailsService);
         //将短信验证码校验器注册到 HttpSecurity
        http.authenticationProvider(appAuthenticationProvider);
    }
}
