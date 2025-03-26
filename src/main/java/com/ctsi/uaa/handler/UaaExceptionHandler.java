package com.ctsi.uaa.handler;

import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.handler.BaseExceptionHandler;
import com.ctsi.uaa.enums.UaaExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/10 11:07
 */
@Slf4j
@ResponseBody
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class UaaExceptionHandler extends BaseExceptionHandler {

    private final static String BAD_CREDENTIALS = "Bad credentials";

    /**
     * 认证异常统一拦截
     *
     * @param e OAuth2Exception
     * @return Result
     */
    @ExceptionHandler(value = OAuth2Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleOAuth2Exception(OAuth2Exception e) {
        String message = e.getMessage();
        if (message.contains(BAD_CREDENTIALS)) {
            message = UaaExceptionEnum.PASSWORD_INVALID.getMessage();
        }
        return Result.fail(message);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleAuthenticationException(AuthenticationException e) {
        String message = e.getMessage();
        return Result.fail(message);
    }

}
