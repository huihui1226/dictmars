package com.ctsi.log.event;

import com.ctsi.log.common.CommonLog;
import com.ctsi.log.common.LoginLogDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 11:28
 */
public class LogEvent extends ApplicationEvent {

    public LogEvent(CommonLog source) {
        super(source);
    }

    public LogEvent(LoginLogDTO source) {
        super(source);
    }
}