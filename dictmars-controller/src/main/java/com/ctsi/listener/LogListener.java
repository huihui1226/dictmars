package com.ctsi.listener;

import com.alibaba.fastjson.JSONObject;
import com.ctsi.core.common.context.MarsContextHolder;
import com.ctsi.core.common.util.$;
import com.ctsi.log.common.CommonLog;
import com.ctsi.log.common.LoginLogDTO;
import com.ctsi.log.event.LogEvent;
import com.ctsi.system.entity.LoginLog;
import com.ctsi.system.entity.SysLog;
import com.ctsi.system.service.ILoginLogService;
import com.ctsi.system.service.ISysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/15 10:52
 */
@Slf4j
@Component
public class LogListener {

    @Autowired
    ISysLogService sysLogService;

    @Autowired
    ILoginLogService loginLogService;

    @Async
    @Order
    @EventListener(LogEvent.class)
    public void saveSysLog(LogEvent event) {
        if (event.getSource() instanceof CommonLog) {
            CommonLog commonLog = (CommonLog) event.getSource();
            printLog(commonLog);
            SysLog sysLog = new SysLog();
            BeanUtils.copyProperties(commonLog, sysLog);
            sysLogService.save(sysLog);
        } else {
            if (event.getSource() instanceof LoginLogDTO) {
                LoginLogDTO loginLogDTO = (LoginLogDTO) event.getSource();
                printLog(loginLogDTO);
                LoginLog loginLog = $.copy(loginLogDTO, LoginLog.class);
                loginLog.setType(loginLogDTO.getType().toString());
                loginLogService.save(loginLog);
            }
        }
    }

    private void printLog(Object o) {
        // 构建成一条长 日志，避免并发下日志错乱
        StringBuilder responseLog = new StringBuilder(300);
        // 日志参数
        List<Object> responseArgs = new ArrayList<>();
        responseLog.append("\n\n================ Log Start  ================\n");
        responseLog.append("userId: {}\n");
        responseArgs.add(MarsContextHolder.getUserId());
        responseLog.append("log: {}\n");
        responseArgs.add(JSONObject.toJSON(o));
        responseLog.append("================  Log End  =================\n");
        // 打印执行时间
        log.info(responseLog.toString(), responseArgs.toArray());
    }
}