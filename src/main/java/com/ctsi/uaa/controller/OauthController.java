package com.ctsi.uaa.controller;

import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.spring.SpringContextHolder;
import com.ctsi.log.common.LoginLogDTO;
import com.ctsi.log.event.LogEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/2 10:42
 */
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Api(tags = "Oauth2管理")
public class OauthController {

    private final TokenEndpoint tokenEndpoint;

    @GetMapping("/token")
    @ApiOperation(value = "用户登录Get", notes = "用户登录Get")
    public Result<?> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.getAccessToken(principal, parameters).getBody());
    }

    @PostMapping("/token")
    @ApiOperation(value = "用户登录Post", notes = "用户登录Post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", required = true, value = "授权类型", paramType = "form"),
            @ApiImplicitParam(name = "username", required = false, value = "用户名", paramType = "form"),
            @ApiImplicitParam(name = "password", required = false, value = "密码", paramType = "form"),
            @ApiImplicitParam(name = "scope", required = true, value = "使用范围", paramType = "form"),
    })
    public Result<?> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody());
    }

    /**
     * 自定义返回格式
     *
     * @param accessToken 　Token
     * @return Result
     */
    private Result<?> custom(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> data = new LinkedHashMap<>(token.getAdditionalInformation());
        data.put("accessToken", token.getValue());
        if (token.getRefreshToken() != null) {
            data.put("refreshToken", token.getRefreshToken().getValue());
        }
        // 推送登录日志
        LoginLogDTO loginLog = LoginLogDTO.success("登录成功");
        SpringContextHolder.publishEvent(new LogEvent(loginLog));
        return Result.data(data);
    }
}