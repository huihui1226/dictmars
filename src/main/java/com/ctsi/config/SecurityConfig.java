package com.ctsi.config;

import com.ctsi.core.security.config.IgnoreUrlPropsConfiguration;
import com.ctsi.core.security.handle.MarsAuthenticationFailureHandler;
import com.ctsi.core.security.handle.MarsAuthenticationSuccessHandler;
import com.ctsi.uaa.app.AppAuthenticationSecurityConfig;
import com.ctsi.uaa.service.impl.UserDetailsServiceImpl;
import com.ctsi.uaa.sms.SmsCodeAuthenticationSecurityConfig;
import com.ctsi.uaa.social.SocialAuthenticationSecurityConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:44
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IgnoreUrlPropsConfiguration ignoreUrlPropsConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
	@Resource
	private AppAuthenticationSecurityConfig appAuthenticationSecurityConfig;
	
	@Resource
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Resource
    private SocialAuthenticationSecurityConfig socialAuthenticationSecurityConfig;

    /**
     * 必须要定义，否则不支持grant_type=password模式
     *
     * @return AuthenticationManager
     */
    @Bean
    @Override
    @SneakyThrows
    public AuthenticationManager authenticationManagerBean() {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationSuccessHandler marsAuthenticationSuccessHandler() {
        return new MarsAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler marsAuthenticationFailureHandler() {
        return new MarsAuthenticationFailureHandler();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config
                = http.requestMatchers().anyRequest()
                .and()
                .formLogin()
                .and()
                .apply(socialAuthenticationSecurityConfig)
                .and()
				.apply(smsCodeAuthenticationSecurityConfig)
				.and()
                .apply(appAuthenticationSecurityConfig)
                .and()
                .authorizeRequests();
        ignoreUrlPropsConfig.getUrls().forEach(url -> {
            config.antMatchers(url).permitAll();
        });
        ignoreUrlPropsConfig.getIgnoreSecurity().forEach(url -> {
            config.antMatchers(url).permitAll();
        });
        config.antMatchers("/**").permitAll();
        config.antMatchers("/**/**").permitAll();
        config.antMatchers("/**/**/**").permitAll();
        config
                //任何请求
                .anyRequest()
                //都需要身份认证
                .authenticated()
                //csrf跨站请求
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }
}
