package com.ctsi.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ctsi.core.common.util.$;
import com.ctsi.core.websocket.BaseWebSocketEndpoint;
import com.ctsi.core.websocket.util.SpringContextHolder;
import com.ctsi.system.entity.Message;
import com.ctsi.system.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebSocket消息通讯
 *
 * @author chenfei
 */
@Slf4j
@Component
@ServerEndpoint(value = "/message/{tenantCode}/{identifier}")
public class MessageEndpoint extends BaseWebSocketEndpoint {

    @OnOpen
    public void openSession(@PathParam("tenantCode") String tenantCode, @PathParam(IDENTIFIER) String userId, Session session) {
        connect(userId, session);
        List<Message> messages;
        final IMessageService messageService = SpringContextHolder.getBean(IMessageService.class);

        messages = messageService.list(Wrappers.<Message>lambdaQuery().eq(Message::getMark, "0")
                    .eq(Message::getReceiveId, userId).orderByAsc(Message::getId));

        if ($.isEmpty(messages)) {
            return;
        }
        // 连接后将未读消息 发送到前端
        messages.forEach(message -> senderMessage(userId,JSON.toJSONString(message)));

    }

    @OnMessage
    public void onMessage(@PathParam(IDENTIFIER) String userId, Session session, String message) {
        log.info("接收到消息 - {}", message);
    }

    @OnClose
    public void onClose(@PathParam(IDENTIFIER) String userId, Session session) {
        disconnect(userId);
    }

    @OnError
    public void onError(@PathParam(IDENTIFIER) String userId, Session session, Throwable throwable) {
        log.info("发生异常： identifier {}", userId);
        log.error(throwable.getMessage(), throwable);
        disconnect(userId);
    }
}
