package com.ctsi.uaa.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.constant.Oauth2Constant;
import com.ctsi.core.common.entity.LoginUser;
import com.ctsi.core.common.exception.MarsException;
import com.ctsi.core.common.util.SecurityUtil;
import com.ctsi.core.common.util.StringUtil;
import com.ctsi.core.redis.core.RedisService;
import com.ctsi.system.constant.SysConstant;
import com.ctsi.config.SocialConfig;
import com.ctsi.system.service.IUserService;
import com.ctsi.uaa.entity.AuthCallback;
import com.ctsi.uaa.enums.UserType;
import com.ctsi.uaa.service.IOnlineUserService;
import com.ctsi.uaa.service.ValidateService;
import com.xkcoding.justauth.AuthRequestFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/2 10:09
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Qualifier("consumerTokenServices")
    private final ConsumerTokenServices consumerTokenServices;

    private final ValidateService validateService;

    private final AuthRequestFactory factory;

    private final SocialConfig socialConfig;

    private final RedisService redisService;

    private final IOnlineUserService onlineUserService;

    private final IUserService userService;

    @GetMapping("/get/user")
    @ApiOperation(value = "用户信息", notes = "用户信息")
    public Result<?> getUser(HttpServletRequest request, HttpServletResponse response) {
        LoginUser loginUser = SecurityUtil.getUsername(request);
        LoginUser loginUserNew = null;
        switch (UserType.valueOf(loginUser.getUserType())) {
            case SYSTEM:
                loginUserNew = userService.getLoginUserByRequest(loginUser.getId(), request);
                break;
            case MOBILE:
                loginUserNew = userService.getLoginUserByRequest(loginUser.getId(),request);
                break;
            default:
                throw new MarsException(SysConstant.NOT_SUPPORT_USERTYPE);
        }
        // 存入redis,以用于mars-starter-auth的PreAuthAspect查询权限使用
        redisService.set(Oauth2Constant.MARS_PERMISSION_PREFIX + loginUser.getAccount(), loginUserNew.getPermissions());
        return Result.data(loginUserNew);
    }

    @GetMapping("/code")
    @ApiOperation(value = "验证码获取", notes = "验证码获取")
    public Result<?> authCode() {
        return validateService.getCode();
    }

    @PostMapping("/logout")
    @ApiOperation(value = "退出登录", notes = "退出登录")
    public Result<?> logout(HttpServletRequest request) {
        if (StringUtil.isNotBlank(SecurityUtil.getHeaderToken(request))) {
            consumerTokenServices.revokeToken(SecurityUtil.getToken(request));
        }
        return Result.success("操作成功");
    }

    /**
     * 验证码下发
     *
     * @param mobile 手机号码
     * @return Result
     */
    @ApiOperation(value = "手机验证码下发", notes = "手机验证码下发")
    @GetMapping("/sms-code")
    public Result<?> smsCode(String mobile) {
        return validateService.getSmsCode(mobile);
    }

    /**
     * 短信验证码校验
     *
     * @param mobile 手机号码
     * @param code 验证码
     * @return Result
     */
    @ApiOperation(value = "短信验证码校验", notes = "短信验证码校验")
    @GetMapping("/checkSms")
    public Result<?> checkSms(String mobile,String code) {
        return validateService.checkSms(mobile,code);
    }

    /**
     * 登录类型
     */
    @GetMapping("/list")
    @ApiOperation(value = "登录类型", notes = "登录类型")
    public Map<String, String> loginType() {
        List<String> oauthList = factory.oauthList();
        return oauthList.stream().collect(Collectors.toMap(oauth -> oauth.toLowerCase() + "登录", oauth -> "http://localhost:10001/mate-uaa/auth/login/" + oauth.toLowerCase()));
    }

    /**
     * 登录
     *
     * @param oauthType 第三方登录类型
     * @param response  response
     * @throws IOException IO异常
     */
    @ApiOperation(value = "第三方登录", notes = "第三方登录")
    @GetMapping("/login/{oauthType}")
    public void login(@PathVariable String oauthType, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(oauthType);
        response.sendRedirect(authRequest.authorize(oauthType + "::" + AuthStateUtils.createState()));
    }

    /**
     * 登录成功后的回调
     *
     * @param oauthType 第三方登录类型
     * @param callback  携带返回的信息
     */
    @ApiOperation(value = "第三方登录回调", notes = "第三方登录回调")
    @GetMapping("/callback/{oauthType}")
    public void callback(@PathVariable String oauthType, AuthCallback callback, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String url = socialConfig.getUrl() + "?code=" + oauthType + "-" + callback.getCode() + "&state=" + callback.getState();
        response.sendRedirect(url);
    }

    @ApiOperation(value = "在线用户", notes = "在线用户回调")
    @PreAuth
    @GetMapping("/online")
    public Result<?> readAllToken(Search search) {
        return Result.data(onlineUserService.onlineUser(search));
    }

    @PreAuth
    @PostMapping("force-logout")
    public Result<?> forceLogout(@RequestBody Map<String, String> map) {
        return Result.condition(consumerTokenServices.revokeToken(map.get("accessToken")));
    }

    public static String checkURL(String url, String domain) {
        if (null == url) {
            return null;
        }
        if (!url.contains(domain)) {
            return null;
        }
        return url;
    }
}
