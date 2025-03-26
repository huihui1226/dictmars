package com.ctsi.log.common;

import cn.hutool.core.util.StrUtil;
import com.ctsi.core.common.context.MarsContextHolder;
import com.ctsi.core.common.util.IPUtil;
import com.ctsi.core.common.util.RequestHolder;
import com.ctsi.log.util.WebInfoUtil;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 10:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class LoginLogDTO implements Serializable {

    private static final long serialVersionUID = -5645180693487257023L;

    /***
     * 用户id
     */
    private Long id;
    /**
     * 账号
     */
    private String userName;
    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 登录类型
     */
    private Type type;
    /**
     * 登录描述
     */
    private String description;

    /**
     * 登录浏览器
     */
    private String ua;
    /**
     * 登录IP
     */
    private String ip;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 登录地址
     */
    private String location;

    private LoginLogDTO setInfo() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return this;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request == null) {
            return this;
        }
        String tempUa = StrUtil.sub(request.getHeader("user-agent"), 0, 500);
        String tempIp = RequestHolder.getHttpServletRequestIpAddress(request);
        String tempLocation = IPUtil.getCityInfo(tempIp);
        this.ua = tempUa;
        this.ip = tempIp;
        this.location = tempLocation;
        this.browser = WebInfoUtil.getBrowser(request);
        this.os = WebInfoUtil.getOs(request);
        return this;
    }

    public static LoginLogDTO success(String description) {
        LoginLogDTO loginLog = LoginLogDTO.builder()
                .tenantId(MarsContextHolder.getTenantId())
                .userName(MarsContextHolder.getUserName())
                .type(Type.SUCCESS).description(description)
                .build().setInfo();
        return loginLog;
    }

    public static LoginLogDTO fail(String description) {
        LoginLogDTO loginLog = LoginLogDTO.builder()
                .tenantId(MarsContextHolder.getTenantId())
                .type(Type.FAIL).description(description)
                .build().setInfo();
        return loginLog;
    }

    public static LoginLogDTO fail(String account, String description) {
        LoginLogDTO loginLog = LoginLogDTO.builder()
                .userName(account).tenantId(MarsContextHolder.getTenantId())
                .type(Type.FAIL).description(description)
                .build().setInfo();
        return loginLog;
    }

    public static LoginLogDTO pwdError(String description) {
        LoginLogDTO loginLog = LoginLogDTO.builder()
                .userName(MarsContextHolder.getUserName())
                .tenantId(MarsContextHolder.getTenantId())
                .type(Type.PWD_ERROR).description(description)
                .build().setInfo();
        return loginLog;
    }

    @Getter
    public enum Type {
        /**
         * 成功
         */
        SUCCESS,
        /**
         * 密码错误
         */
        PWD_ERROR,
        /**
         * 失败
         */
        FAIL
    }
}