package com.smileframework.bullet.rpc.spring.provider.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "reabam.framework.bullet.server")
public class BulletProviderProperties {

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
     * 工作线程池最小线程数
     */
    private int workCorePoolSize = 15;

    /**
     * 工作线程池最大线程数
     */
    private int workMaxPoolSize = 40;

    /**
     * 工作线程 空闲存活时间
     */
    private int workKeepAliveTimeSecond = 60;

    /**
     * 工作线程 任务队列最大数量
     */
    private int workBlockingQueueSize = 10;

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

    /**
     * provider 执行日志
     */
    private Boolean enableInvokeLog = true;


}
