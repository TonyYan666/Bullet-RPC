package com.smileframework.bullet.rpc.spring.provider;

import com.smileframework.bullet.rpc.spring.provider.properties.BulletProviderProperties;
import com.smileframework.bullet.rpc.provider.BulletProviderContext;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.server.BulletServerContext;
import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


public class SpringBulletProviderContext extends BulletProviderContext {

    public BulletServerContext init(BulletProviderProperties providerProperties, ContentConvertManager contentConvertManager) {
        BulletTransportServerConfig transportServerConfig = new BulletTransportServerConfig();
        transportServerConfig.setPort(providerProperties.getPort());
        transportServerConfig.setShutdownTimeout(providerProperties.getShutdownTimeout());
        transportServerConfig.setIdleTimeout(providerProperties.getIdleTimeout());
        transportServerConfig.setBulletTransportServerHandlerThreadNum(providerProperties.getBulletTransportServerHandlerThreadNum());
        transportServerConfig.setIoLoopThreadNum(providerProperties.getIoLoopThreadNum());
        transportServerConfig.setEnableTransportAuthentication(providerProperties.getEnableTransportAuthentication());
        transportServerConfig.setShutdownUnauthenticatedConnection(providerProperties.getShutdownUnauthenticatedConnection());
        return super.init(transportServerConfig, contentConvertManager);
    }

    @PostConstruct
    @Override
    public void doOpen() {
        super.doOpen();
    }

    @PreDestroy
    @Override
    public void doClose() {
        super.doClose();
    }
}
