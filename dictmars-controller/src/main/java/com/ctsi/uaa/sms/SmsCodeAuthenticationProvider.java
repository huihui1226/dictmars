package com.ctsi.uaa.sms;

import com.ctsi.core.security.userdetails.MarsUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

/**
 * 短信验证码验证提供者
 *
 * @author pangu
 */
@AllArgsConstructor
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

	private final MarsUserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

		/**
		 * 调用 {@link UserDetailsService}
		 */
		UserDetails user = userDetailsService.loadUserByMobile((String) authenticationToken.getPrincipal());

		if (Objects.isNull(user)) {
			throw new InternalAuthenticationServiceException("手机号或验证码错误");
		}

		SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());

		authenticationResult.setDetails(authenticationToken.getDetails());

		return authenticationResult;

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
