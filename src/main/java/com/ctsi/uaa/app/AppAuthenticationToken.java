package com.ctsi.uaa.app;

import lombok.SneakyThrows;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:49
 */
public class AppAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -2721661528106186767L;
    private final Object principal;

    public AppAuthenticationToken(String username) {
        super(null);
        this.principal = username;
        this.setAuthenticatedSelf(false);
    }

    public AppAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.principal;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    @SneakyThrows
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    private void setAuthenticatedSelf(boolean isAuthenticated) {
        this.setAuthenticated(isAuthenticated);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}

