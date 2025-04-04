package com.ctsi.uaa.social;

import com.ctsi.core.common.exception.MarsException;
import com.ctsi.core.security.userdetails.MarsUserDetailsService;
import com.ctsi.uaa.enums.SourceType;
import lombok.AllArgsConstructor;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

/**
 * 社交登录验证提供者
 *
 * @author pangu
 */
@AllArgsConstructor
public class SocialAuthenticationProvider implements AuthenticationProvider {

    private final MarsUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SocialAuthenticationToken authenticationToken = (SocialAuthenticationToken) authentication;
        UserDetails user = null;
        String source = ((AuthUser) authenticationToken.getPrincipal()).getSource();
        switch(SourceType.value(source)){
            case TELECOM:
                user = userDetailsService.loadUserBySocial(((AuthUser) authenticationToken.getPrincipal()).getUsername());
                break;
            case HIAUTH:
                user = userDetailsService.loadUserByUsername(((AuthUser) authenticationToken.getPrincipal()).getUsername());
                break;
            default:
                throw new MarsException("不支持的登录第三方");
        }
        if (Objects.isNull(user)) {
            throw new InternalAuthenticationServiceException("社交登录错误");
        }
        SocialAuthenticationToken authenticationResult = new SocialAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
