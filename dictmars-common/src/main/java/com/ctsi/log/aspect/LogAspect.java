package com.ctsi.log.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.context.MarsContextHolder;
import com.ctsi.core.common.util.$;
import com.ctsi.core.common.util.RequestHolder;
import com.ctsi.core.common.util.StringPool;
import com.ctsi.core.common.util.spring.SpringContextHolder;
import com.ctsi.core.common.util.ttl.ThreadLocalUtil;
import com.ctsi.log.annotation.Log;
import com.ctsi.log.common.CommonLog;
import com.ctsi.log.common.ThreadLocalParam;
import com.ctsi.log.enums.LogTypeEnum;
import com.ctsi.log.event.LogEvent;
import com.ctsi.log.util.LogUtil;
import com.ctsi.log.util.WebInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 10:43
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    private static final String TL_COMMON_LOG_KEY = "tl_common_log_key";
    private static final int MIN_LENGTH = 0;
    // 操作日志的最大长度
    private static final int MAX_LENGTH = 5535;
    private static final int MAX_UA_LENGTH = 50;

    public static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    /**
     * SpEL表达式解析
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    /**
     * 获取方法参数定义的名称
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    @Pointcut("@annotation(com.ctsi.log.annotation.Log)")
    public void pointcut() {
    }

    @AfterReturning(returning = "ret", pointcut = "pointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch((logs) -> {
            Log log = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, log)) {
                return;
            }
            CommonLog commonLog = get();
            if (!(ret instanceof Result)) {
                commonLog.setType(LogTypeEnum.COMMON.getCode());
                if (log.response()) {
                    commonLog.setOperation(getText($.toStr(ret == null ? StringPool.EMPTY : ret)));
                }
            } else {
                Result result = Convert.convert(Result.class, ret);
                if (result.isSuccess()) {
                    commonLog.setType(LogTypeEnum.COMMON.getCode());
                } else {
                    commonLog.setType(LogTypeEnum.EXCEPTION.getCode());
                    commonLog.setException(result.getMsg());
                }
                if (log.response()) {
                    commonLog.setOperation(getText(JSONObject.toJSONString(result)));
                }
            }
            publishEvent(commonLog);
        });
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        tryCatch((ext) -> {
            Log log = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, log)) {
                return;
            }

            CommonLog commonLog = get();
            commonLog.setType(LogTypeEnum.EXCEPTION.getCode());

            // 遇到错误时，请求参数若为空，则记录
            if (!log.request() && log.requestByError() && $.isEmpty(commonLog.getParams())) {
                Object[] args = joinPoint.getArgs();
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String strArgs = getArgs(args, request);
                commonLog.setParams(strArgs);
            }
            // 异常对象
            commonLog.setException(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));
            publishEvent(commonLog);
        });
    }

    @Before(value = "pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        tryCatch(val -> {
            Log log = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, log)) {
                return;
            }
            CommonLog commonLog = buildCommonLog(joinPoint, log);
            ThreadLocalUtil.set(TL_COMMON_LOG_KEY, commonLog);
        });
    }

    private CommonLog buildCommonLog(JoinPoint joinPoint, Log log) {
        // 开始时间
        CommonLog commonLog = get();
        commonLog.setCreateBy(String.valueOf(MarsContextHolder.getUserId()));
        commonLog.setUserName(MarsContextHolder.getUserName());
        setDescription(joinPoint, log, commonLog);
        // 类名
        commonLog.setClassPath(joinPoint.getTarget().getClass().getName());
        // 方法名
        commonLog.setMethodName(joinPoint.getSignature().getName());

        HttpServletRequest request = setParams(joinPoint, log, commonLog);
        commonLog.setIp(RequestHolder.getHttpServletRequestIpAddress(request));
        commonLog.setUrl(URLUtil.getPath(request.getRequestURI()));
        commonLog.setMethodType(request.getMethod());
        commonLog.setBrowser(WebInfoUtil.getBrowser(request));
        commonLog.setOs(WebInfoUtil.getOs(request));
        commonLog.setStartTime(LocalDateTime.now());
        return commonLog;
    }

    @NonNull
    private HttpServletRequest setParams(JoinPoint joinPoint, Log log, CommonLog commonLog) {
        // 参数
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.currentRequestAttributes(), "只能在Spring Web环境使用@SysLog记录日志")).getRequest();
        if (log.request()) {
            String strArgs = getArgs(args, request);
            commonLog.setParams(strArgs);
        }
        return request;
    }

    private void setDescription(JoinPoint joinPoint, Log log, CommonLog commonLog) {
        String controllerDesc = "";
        Api api = joinPoint.getTarget().getClass().getAnnotation(Api.class);
        if (api != null) {
            String[] tags = api.tags();
            if (ArrayUtil.isNotEmpty(tags)) {
                controllerDesc = tags[0];
            }
        }
        String controllerMethodDesc = LogUtil.getDescribe(log);

        // 如果为空，则获ApiOperation里面的标注
        if ($.isBlank(controllerMethodDesc)) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            ApiOperation apiOperation = methodSignature.getMethod().getAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                controllerMethodDesc = apiOperation.value();
            }
        }

        if ($.isNotEmpty(controllerMethodDesc) && StrUtil.contains(controllerMethodDesc, StringPool.HASH)) {
            // 获取方法参数值
            Object[] args = joinPoint.getArgs();

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            controllerMethodDesc = getValBySpEL(controllerMethodDesc, methodSignature, args);
        }

        if ($.isEmpty(controllerDesc)) {
            commonLog.setTitle(controllerMethodDesc);
        } else {
            if (log.controllerApiValue()) {
                commonLog.setTitle(controllerDesc + "-" + controllerMethodDesc);
            } else {
                commonLog.setTitle(controllerMethodDesc);
            }
        }
    }

    private void publishEvent(CommonLog commonLog) {
        commonLog.setFinishTime(LocalDateTime.now());
        commonLog.setExecuteTime(commonLog.getStartTime().until(commonLog.getFinishTime(), ChronoUnit.MILLIS));
        SpringContextHolder.publishEvent(new LogEvent(commonLog));
        ThreadLocalUtil.remove(TL_COMMON_LOG_KEY);
    }

    private CommonLog get() {
        CommonLog commonLog = ThreadLocalUtil.get(TL_COMMON_LOG_KEY);
        if (commonLog == null) {
            return new CommonLog();
        }
        return commonLog;
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录操作日志异常", e);
            ThreadLocalUtil.remove(TL_COMMON_LOG_KEY);
        }
    }

    /**
     * 检查是否需要记录日志
     *
     * @param joinPoint 端点
     * @param log       操作日志
     * @return true 表示要记录日志
     */
    private boolean check(JoinPoint joinPoint, Log log) {
        if (log == null || !log.enabled()) {
            return true;
        }
        // 读取目标类上的注解
        Log targetClass = joinPoint.getTarget().getClass().getAnnotation(Log.class);
        // 加上　log == null 会导致父类上的方法永远需要记录日志
        return targetClass != null && !targetClass.enabled();
    }

    private String getText(String val) {
        return StrUtil.sub(val, MIN_LENGTH, MAX_LENGTH);
    }

    /**
     * 解析spEL表达式
     *
     * @param spEl            表达式
     * @param methodSignature 方法签名
     * @param args            参数
     * @return {String}
     */
    private String getValBySpEL(String spEl, MethodSignature methodSignature, Object[] args) {
        try {
            //获取方法形参名数组
            String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = spelExpressionParser.parseExpression(spEl);
                // Spring 的表达式上下文对象
                EvaluationContext context = new StandardEvaluationContext();
                // 给上下文赋值
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }
                ThreadLocalParam tlp = new ThreadLocalParam();
                BeanUtil.fillBeanWithMap(MarsContextHolder.getLocalMap(), tlp, true);
                context.setVariable("threadLocal", tlp);
                Object value = expression.getValue(context);
                return value == null ? spEl : value.toString();
            }
        } catch (Exception e) {
            log.warn("解析操作日志el表达式出错", e);
        }
        return spEl;
    }

    private Method resolveMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();

        Method method = getDeclaredMethod(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }

    private Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }

    /**
     * 获取请求参数
     *
     * @param args    参数列表
     * @param request request请求
     * @return {String}
     */
    private String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = StringPool.EMPTY;
        Object[] params = Arrays.stream(args).filter(item -> !(item instanceof ServletRequest || item instanceof ServletResponse)).toArray();

        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JSONObject.toJSONString(params);
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(params);
            } catch (Exception ex) {
                log.warn("解析参数异常", ex);
            }
        }
        return strArgs;
    }
}