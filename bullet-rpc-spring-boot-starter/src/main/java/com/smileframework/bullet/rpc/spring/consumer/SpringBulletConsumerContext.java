package com.smileframework.bullet.rpc.spring.consumer;

import com.smileframework.bullet.rpc.spring.consumer.properties.BulletConsumerProperties;
import com.smileframework.bullet.rpc.consumer.BulletConsumerContext;
import com.smileframework.bullet.transport.client.config.BulletClientConfig;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


public class SpringBulletConsumerContext extends BulletConsumerContext {

    public SpringBulletConsumerContext(BulletConsumerProperties consumerProperties, ContentConvertManager contentConvertManager, ServerConnectionManager serverConnectionManager) {
        super(contentConvertManager);
        super.setServerConnectionManager(serverConnectionManager);
        BulletClientConfig clientConfig = new BulletClientConfig();
        clientConfig.setHandshakeTimeout(consumerProperties.getHandshakeTimeout());
        clientConfig.setShutdownTimeout(consumerProperties.getShutdownTimeout());
        clientConfig.setIdleTimeout(consumerProperties.getIdleTimeout());
        clientConfig.setHeartbeatTimeout(consumerProperties.getHeartbeatTimeout());
        clientConfig.setConnectTimeout(consumerProperties.getConnectionTimeout());
        this.setClientConfig(clientConfig);
    }

    private SpringBulletConsumerContext() {
        super();
    }

    @PostConstruct
    public void start() {
        super.start();
    }


    @PreDestroy
    @Override
    public void shutdown() {
        super.shutdown();
    }
}
