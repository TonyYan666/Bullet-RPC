package com.smileframework.bullet.rpc.spring.consumer.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "reabam.framework.bullet.client")
public class BulletConsumerProperties {

    /**
     * 握手超时时间
     */
    private Duration handshakeTimeout = Duration.ofSeconds(10);

    /**
     * 连接创建超时时间
     */
    private Duration connectionTimeout = Duration.ofSeconds(2);

    /**
     * 停止服务超时时间
     */
    private Duration shutdownTimeout = Duration.ofSeconds(3);

    /**
     * 空闲时间
     */
    private Duration idleTimeout = Duration.ofMinutes(40);

    /**
     * 心跳超时
     */
    private Duration heartbeatTimeout = Duration.ofSeconds(2);

    /**
     * consumer请求日志
     */
    private Boolean enableRequestLog = true;


}
