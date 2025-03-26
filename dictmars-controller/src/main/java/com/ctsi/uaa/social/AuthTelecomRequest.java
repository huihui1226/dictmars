package com.ctsi.uaa.social;

import com.alibaba.fastjson.JSONObject;
import com.ctsi.common.util.RSAUtils;
import lombok.SneakyThrows;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.HttpUtils;
import me.zhyd.oauth.utils.UrlBuilder;

import java.util.HashMap;
import java.util.Map;

public class AuthTelecomRequest extends AuthDefaultRequest {


    public AuthTelecomRequest(AuthConfig config) {
        super(config, AuthCustomSource.TELECOM);
    }

    public AuthTelecomRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthCustomSource.TELECOM, authStateCache);
    }

    @Override
    protected String doPostAuthorizationCode(String code) {
        String baseUrl = UrlBuilder.fromBaseUrl(this.source.accessToken()).build();
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", this.config.getRedirectUri());
        params.put("client_id", this.config.getClientId());
        return (new HttpUtils(this.config.getHttpConfig())).post(baseUrl,params,true);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String body = doPostAuthorizationCode(authCallback.getCode());
        JSONObject object = JSONObject.parseObject(body);

        this.checkResponse(object);

        return AuthToken.builder()
                .accessToken(object.getString("access_token"))
                .refreshToken(object.getString("refresh_token"))
                .idToken(object.getString("id_token"))
                .tokenType(object.getString("token_type"))
                .scope(object.getString("scope"))
                .build();
    }

    @SneakyThrows
    @Override
    protected String doGetUserInfo(AuthToken authToken) {
        return RSAUtils.decryptByPrivateKeyStr(authToken.getAccessToken(), this.config.getClientSecret());
    }


    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String body = doGetUserInfo(authToken);
        JSONObject object = JSONObject.parseObject(body);
        JSONObject user = object.getJSONObject("user");
        this.checkResponse(object);

        return AuthUser.builder()
                .uuid(user.getString("uuid"))
                .username(user.getString("staffAccount"))
                .nickname(user.getString("name"))
                .avatar(user.getString("avatar_url"))
                .blog(user.getString("web_url"))
                .company(user.getString("organization"))
                .location(user.getString("location"))
                .email(user.getString("innerEmail"))
                .remark(user.getString("bio"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }

    private void checkResponse(JSONObject object) {
        // oauth/token 验证异常
        if (object.containsKey("error")) {
            throw new AuthException(object.getString("error_description"));
        }
        // user 验证异常
        if (object.containsKey("message")) {
            throw new AuthException(object.getString("message"));
        }
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     * @since 1.11.0
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(super.authorize(state))
                .queryParam("scope", "all")
                .build();
    }

}

