package com.ctsi.log.annotation;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 10:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 描述
     *
     * @return {String}
     */
    String value() default "";

    /**
     * 异常描述
     *
     * @return {String}
     */
    String exception() default "MarsCloud系统内部异常";

    /**
     * 是否启用
     *
     * @return {boolean}
     */
    boolean enabled() default true;

    /**
     * 记录执行参数
     *
     * @return {boolean}
     */
    boolean request() default true;

    /**
     * 记录响应参数
     *
     * @return {boolean}
     */
    boolean response() default true;

    /**
     * 是否拼接Controller类的描述值
     *
     * @return {boolean}
     */
    boolean controllerApiValue() default true;

    /**
     * 当 request = false时，方法报错记录请求参数
     *
     * @return {boolean}
     */
    boolean requestByError() default true;
}
