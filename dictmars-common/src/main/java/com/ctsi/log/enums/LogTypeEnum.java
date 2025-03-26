package com.ctsi.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 11:30
 */
@Getter
@AllArgsConstructor
public enum LogTypeEnum {

    /**
     * 日志操作类型
     */
    COMMON("OPT", "操作类型"),

    EXCEPTION("EX", "异常类型");

    private final String code;
    private final String message;
}
