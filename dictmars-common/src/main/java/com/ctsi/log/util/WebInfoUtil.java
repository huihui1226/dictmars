package com.ctsi.log.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 10:46
 */
@UtilityClass
public class WebInfoUtil {

    private final static String USER_AGENT = "User-Agent";

    /**
     * 获取浏览器信息
     *
     * @param request http请求
     * @return 浏览器名称
     */
    public String getBrowser(HttpServletRequest request) {
        Browser browser = getUserAgent(request).getBrowser();
        return browser.getName() + "-" + browser.getVersion(request.getHeader(USER_AGENT));
    }

    /**
     * 获取操作系统信息
     *
     * @param request http请求
     * @return 操作系统名称
     */
    public String getOs(HttpServletRequest request) {
        OperatingSystem os = getUserAgent(request).getOperatingSystem();
        return os.getName();
    }

    private UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader(USER_AGENT));
    }
}
