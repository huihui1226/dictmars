package com.ctsi;

import com.ctsi.core.websocket.redis.EnableRedisWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 11:36
 */
@SpringBootApplication
public class MarsBootServer {
    public static void main(String[] args) {
        SpringApplication.run(MarsBootServer.class, args);
    }
}
