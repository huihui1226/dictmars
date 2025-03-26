package com.ctsi.uaa.sms;

import com.ctsi.core.common.constant.Oauth2Constant;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 短信验证码验证过滤器
 *
 * @author pangu
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	/**
	 * 请求中的参数
	 */
	private String mobileParameter = Oauth2Constant.DEFAULT_PARAMETER_NAME_MOBILE;

	private boolean postOnly = true;

	private static final String POST = "post";

	public SmsCodeAuthenticationFilter() {
		super(new AntPathRequestMatcher(Oauth2Constant.OAUTH_MOBILE, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (request.getMethod() != null && !POST.equals(request.getMethod())) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		// 获取请求中的参数值
		String mobile = obtainMobile(request);

		if (Objects.isNull(mobile)) {
			mobile = "";
		}

		mobile = mobile.trim();

		SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	/**
	 * 获取手机号
	 */
	protected String obtainMobile(HttpServletRequest request) {
		return request.getParameter(mobileParameter);
	}

	protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	public void setMobileParameter(String mobileParameter) {
		Assert.hasText(mobileParameter, "Mobile parameter must not be empty or null");
		this.mobileParameter = mobileParameter;
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public final String getMobileParameter() {
		return mobileParameter;
	}
}
