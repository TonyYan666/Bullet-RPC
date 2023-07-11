package com.smileframework.bullet.transport.client.connection.properties;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class ServerConnectionProperties {

    /**
     * 停止服务超时时间
     */
    private Duration shutdownTimeout = Duration.ofSeconds(3);

    /**
     * 空闲时间
     */
    private Duration idleTimeout = Duration.ofSeconds(2);

    /**
     * 心跳超时
     */
    private Duration heartbeatTimeout = Duration.ofSeconds(280);

    /**
     * 握手超时时间
     */
    private Duration handshakeTimeout = Duration.ofSeconds(10);

    /**
     * 服务地址
     */
    private String serverAddress;

    /**
     * 服务端号
     */
    private int serverPort;


}
