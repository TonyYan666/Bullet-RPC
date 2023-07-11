package com.smileframework.bullet.transport.client;

import com.smileframework.bullet.transport.client.config.BulletClientConfig;
import com.smileframework.bullet.transport.client.connection.handshake.HandshakeInfoProvider;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;

public class BulletClientContextBuilder {

    private BulletClientContext context;

    public BulletClientContextBuilder() {
        this.context = new BulletClientContext();
    }

    public BulletClientContextBuilder setContentConvertManager(ContentConvertManager contentConvertManager) {
        this.context.setContentConvertManager(contentConvertManager);
        return this;
    }

    public BulletClientContextBuilder setResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
        this.context.setResponseErrorHandler(responseErrorHandler);
        return this;
    }


    public BulletClientContextBuilder setHandshakeInfoProvider(HandshakeInfoProvider handshakeInfoProvider) {
        this.context.setHandshakeInfoProvider(handshakeInfoProvider);
        return this;
    }

    public BulletClientContextBuilder setBulletClientConfig(BulletClientConfig bulletClientConfig) {
        this.context.setClientConfig(bulletClientConfig);
        return this;
    }

    public BulletClientContext build() {
        if (this.context.getClientConfig() == null) {
            throw new BulletConfigException("Bullet client context config error, client config didn't set.");
        }
        if (this.context.getContentConvertManager() == null) {
            throw new BulletConfigException("Bullet client context config error, ContentConvertManager didn't set.");
        }
        if (this.context.getHandshakeInfoProvider() == null) {
            throw new BulletConfigException("Bullet client context config error, HandshakeInfoProvider didn't set.");
        }
        if (this.context.getResponseErrorHandler() == null) {
            throw new BulletConfigException("Bullet client context config error, ResponseErrorHandler didn't set.");
        }
        return this.context;
    }


}
