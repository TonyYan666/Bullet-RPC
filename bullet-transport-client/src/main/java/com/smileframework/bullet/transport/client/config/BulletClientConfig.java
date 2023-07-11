package com.smileframework.bullet.transport.client.config;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class BulletClientConfig {

    /**
     * 握手超时时间
     */
    private Duration handshakeTimeout = Duration.ofSeconds(10);

    /**
     * 停止服务超时时间
     */
    private Duration shutdownTimeout = Duration.ofSeconds(3);

    /**
     * 空闲时间
     */
    private Duration idleTimeout = Duration.ofHours(1);

    /**
     * 心跳超时
     */
    private Duration heartbeatTimeout = Duration.ofSeconds(2);

    /**
     * 连接获取超时时间
     */
    private Duration connectTimeout = Duration.ofSeconds(2);

}
