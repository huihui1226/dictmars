package com.ctsi.log.util;

import com.ctsi.core.common.util.StringPool;
import com.ctsi.log.annotation.Log;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 10:46
 */
@Slf4j
@UtilityClass
public class LogUtil {

    /**
     * 优先从子类获取@Log 注解
     * <p>
     * 1、若子类重写了该方，有标记就记录日志，没有标记就忽略日志
     * 2、若子类没有重写该方法，就从父类获取，父亲有标记就记录日志，没有标记就忽略日志
     * </p>
     *
     * @param point 端点
     * @return 注解
     */
    public Log getTargetAnnotation(JoinPoint point) {
        try {
            Log annotation = null;
            if (point.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                if (method != null) {
                    annotation = method.getAnnotation(Log.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            log.warn("获取{}.{}的 @Log 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), e);
            return null;
        }
    }

    /**
     * 获取操作信息
     *
     * @param point 端点
     * @return String
     */
    public String getDescribe(JoinPoint point) {
        Log annotation = getTargetAnnotation(point);
        if (annotation == null) {
            return StringPool.EMPTY;
        }
        return annotation.value();
    }

    public String getDescribe(Log annotation) {
        if (annotation == null) {
            return StringPool.EMPTY;
        }
        return annotation.value();
    }
}
