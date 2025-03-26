package com.ctsi.config;

import com.ctsi.core.common.constant.Oauth2Constant;
import com.ctsi.core.redis.core.RedisService;
import com.ctsi.core.security.userdetails.MarsUser;
import com.ctsi.uaa.granter.CaptchaTokenGranter;
import com.ctsi.uaa.granter.SmsCodeTokenGranter;
import com.ctsi.uaa.granter.SocialTokenGranter;
import com.ctsi.uaa.service.impl.ClientDetailsServiceImpl;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.*;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:43
 */
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private final ClientDetailsServiceImpl clientService;

    private final RedisConnectionFactory redisConnectionFactory;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final RedisService redisService;

    private final AuthRequestFactory factory;

    /**
     * 配置token存储到redis中
     */
    @Bean
    public RedisTokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // token增强链
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        // 把jwt增强，与额外信息增强加入到增强链
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
        endpoints
                .authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancerChain)
                .accessTokenConverter(jwtAccessTokenConverter())
                .userDetailsService(userDetailsService)
                .tokenGranter(tokenGranter(endpoints))
                .tokenStore(redisTokenStore())
                .reuseRefreshTokens(Boolean.FALSE);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 允许表单认证请求
                .allowFormAuthenticationForClients()
                // spel表达式 访问公钥端点（/auth/token_key）需要认证
                .tokenKeyAccess("isAuthenticated()")
                // spel表达式 访问令牌解析端点（/auth/check_token）需要认证
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clientService.setSelectClientDetailsSql(Oauth2Constant.SELECT_CLIENT_DETAIL_SQL);
        clientService.setFindClientDetailsSql(Oauth2Constant.FIND_CLIENT_DETAIL_SQL);
        clients.withClientDetails(clientService);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(Oauth2Constant.SIGN_KEY);
        return jwtAccessTokenConverter;
    }

    /**
     * 重点
     * 先获取已经有的五种授权，然后添加我们自己的进去
     *
     * @param endpoints AuthorizationServerEndpointsConfigurer
     * @return TokenGranter
     */
    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
        granters.add(new CaptchaTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), redisService));
        granters.add(new SmsCodeTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), redisService));
        granters.add(new SocialTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), redisService, factory));
        return new CompositeTokenGranter(granters);
    }

    /**
     * jwt token增强，添加额外信息
     *
     * @return
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new TokenEnhancer() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

                // 添加额外信息的map
                final Map<String, Object> additionMessage = new HashMap<>(4);
                // 获取当前登录的用户
                MarsUser user = (MarsUser) oAuth2Authentication.getUserAuthentication().getPrincipal();

                // 如果用户不为空 则把id放入jwt token中
                if (user != null) {
                    additionMessage.put(Oauth2Constant.ADD_USER_ID, String.valueOf(user.getId()));
                    additionMessage.put(Oauth2Constant.ADD_USER_NAME, user.getUsername());
                    additionMessage.put(Oauth2Constant.ADD_AVATAR, user.getAvatar());
                    additionMessage.put(Oauth2Constant.ADD_TYPE, user.getType());
                    additionMessage.put(Oauth2Constant.ADD_IP, user.getIp());
                    additionMessage.put(Oauth2Constant.ADD_ACCOUNT,user.getUsername());
                }
                ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionMessage);
                return oAuth2AccessToken;
            }
        };
    }
}
