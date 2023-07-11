package com.smileframework.bullet.transport.server.config;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class BulletTransportServerConfig {

    /**
     * 端口号
     */
    private int port = 2186;

    /**
     * 停止服务超时时间
     */
    private Duration shutdownTimeout = Duration.ofSeconds(3);

    /**
     * 空闲时间
     */
    private Duration idleTimeout = Duration.ofSeconds(300);

    /**
     * BulletTransportServerHandler 工作线程
     */
    private int bulletTransportServerHandlerThreadNum = 4;

    /**
     * IO线程数
     */
    private int ioLoopThreadNum = 6;

    /**
     * 是否开启传输认证
     */
    private Boolean enableTransportAuthentication = false;

    /**
     * 是否关闭认证失败连接
     */
    private Boolean shutdownUnauthenticatedConnection = false;

}
