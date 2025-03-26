package com.ctsi.uaa.granter;

import com.ctsi.core.common.exception.MarsException;
import com.ctsi.core.redis.core.RedisService;
import com.ctsi.core.web.util.WebUtil;
import com.ctsi.uaa.app.AppAuthenticationToken;
import com.ctsi.uaa.enums.UaaExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
 * @date: 2021/8/10 10:55
 */
@Slf4j
public class AppTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "app";

    private final AuthenticationManager authenticationManager;

    private RedisService redisService;

    public AppTokenGranter(AuthenticationManager authenticationManager,
                           AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                           OAuth2RequestFactory requestFactory, RedisService redisService) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.redisService = redisService;
    }

    protected AppTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
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
        String username = parameters.get("username");
        // Protect from downstream leaks of password
        parameters.remove("password");

        //@Todo 此处可以验证密码是否正确

        Authentication userAuth = new AppAuthenticationToken(username);
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
