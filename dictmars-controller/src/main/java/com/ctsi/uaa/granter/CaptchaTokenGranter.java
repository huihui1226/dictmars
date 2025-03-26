package com.ctsi.uaa.granter;

import com.ctsi.core.common.constant.Oauth2Constant;
import com.ctsi.core.common.exception.MarsException;
import com.ctsi.core.common.util.$;
import com.ctsi.core.redis.core.RedisService;
import com.ctsi.core.web.util.WebUtil;
import com.ctsi.uaa.enums.UaaExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:58
 */
public class CaptchaTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "captcha";

    private final AuthenticationManager authenticationManager;

    private RedisService redisService;

    public CaptchaTokenGranter(AuthenticationManager authenticationManager,
                               AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory, RedisService redisService) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.redisService = redisService;
    }

    protected CaptchaTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                  ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        HttpServletRequest request = WebUtil.getRequest();

        if (null == request) {
            throw new MarsException(UaaExceptionEnum.PARAMS_NOT_EXIST);
        }

        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
        String type =  parameters.get("type");
        
        if($.isBlank(type) || !type.equals("APP")) {
        	// 增加验证码判断
            String key = request.getHeader(Oauth2Constant.CAPTCHA_HEADER_KEY);
            String code = request.getHeader(Oauth2Constant.CAPTCHA_HEADER_CODE);
            Object codeFromRedis = redisService.get(Oauth2Constant.CAPTCHA_KEY + key);
        	if($.isBlank(code)) {
        		throw new MarsException(UaaExceptionEnum.VERIFICATION_CODE_IS_EMPTY);
        	}
        	if ($.isNull(codeFromRedis)) {
        		throw new MarsException(UaaExceptionEnum.VERIFICATION_CODE_EXPIRED);
        	}
        	if (!StringUtils.equalsIgnoreCase(code, codeFromRedis.toString())) {
        		throw new MarsException(UaaExceptionEnum.VERIFICATION_CODE_ERROR);
        	}
        	redisService.del(Oauth2Constant.CAPTCHA_KEY + key);
        }

        //Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");
        String password = parameters.get("password");
        // Protect from downstream leaks of password
        parameters.remove("password");

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        // If the username/password are wrong the spec says we should send 400/invalid grant

        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }

}
