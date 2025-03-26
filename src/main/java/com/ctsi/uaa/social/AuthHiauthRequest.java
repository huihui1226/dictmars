package com.ctsi.uaa.social;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.xkcoding.http.support.HttpHeader;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.HttpUtils;
import me.zhyd.oauth.utils.UrlBuilder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class AuthHiauthRequest extends AuthDefaultRequest {


    public AuthHiauthRequest(AuthConfig config) {
        super(config, AuthCustomSource.HIAUTH);
    }

    public AuthHiauthRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthCustomSource.HIAUTH, authStateCache);
    }


    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String response = this.doPostAuthorizationCode(authCallback.getCode());
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        this.checkResponse(accessTokenObject);
        return  AuthToken.builder().accessToken(accessTokenObject.getString("access_token")).refreshToken(accessTokenObject.getString("refresh_token")).scope(accessTokenObject.getString("scope")).tokenType(accessTokenObject.getString("token_type")).expireIn(accessTokenObject.getIntValue("expires_in")).build();
    }



    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String body = super.doGetUserInfo(authToken);
        JSONObject object = JSONObject.parseObject(body);
        JSONObject user = object.getJSONObject("data");
        this.checkResponse(object);

        return AuthUser.builder()
                .uuid(user.getString("userId"))
                .username(user.getString("username"))
                .nickname(user.getString("name"))
                .avatar(user.getString("avatar"))
                .location(user.getString("location"))
                .gender(AuthUserGender.UNKNOWN)
                .remark(user.getString("userType"))
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
                .queryParam("scope", "AUTH")
                .build();
    }

    @Override
    public String revokeUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(this.source.revoke()).build();
    }

    @Override
    public AuthResponse revoke(AuthToken authToken) {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.add("Authorization", "Basic " + Base64.encode(String.format("%s:%s", this.config.getClientId(), this.config.getClientSecret()).getBytes(Charset.forName("UTF-8"))));
        Map<String,String> param = new HashMap<String,String>();
        param.put("token", authToken.getAccessToken());
        new HttpUtils(this.config.getHttpConfig()).post(this.revokeUrl(authToken),param,httpHeader,true);
        return null;
    }

}

