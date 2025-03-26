package com.ctsi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 10:43
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "social.vue")
public class SocialConfig {
    private String url;
    private String domain;
}
