package com.ctsi.uaa.app;

import com.ctsi.core.security.userdetails.MarsUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:48
 */
@RequiredArgsConstructor
public class AppAuthenticationProvider implements AuthenticationProvider {

    private final MarsUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AppAuthenticationToken authenticationToken = (AppAuthenticationToken) authentication;

        /**
         * 调用 {@link UserDetailsService}
         */
        UserDetails user = userDetailsService.loadUserByAppLogin((String) authenticationToken.getPrincipal());

        if (Objects.isNull(user)) {
            throw new InternalAuthenticationServiceException("APP用户不存在！");
        }

        AppAuthenticationToken authenticationResult = new AppAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AppAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

