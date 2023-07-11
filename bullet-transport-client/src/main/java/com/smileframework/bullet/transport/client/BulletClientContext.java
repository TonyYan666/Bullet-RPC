package com.smileframework.bullet.transport.client;

import com.smileframework.bullet.transport.client.config.BulletClientConfig;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.connection.handshake.HandshakeInfoProvider;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.client.error.impl.BaseResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.config.ClientContextNotReadyException;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import lombok.Getter;

public class BulletClientContext {

    @Getter
    private ContentConvertManager contentConvertManager;

    @Getter
    private ResponseErrorHandler responseErrorHandler;

    @Getter
    private HandshakeInfoProvider handshakeInfoProvider;

    @Getter
    private BulletClientConfig clientConfig;

    @Getter
    private ServerConnectionManager serverConnectionManager;

    public BulletClientContext() {
        this.responseErrorHandler = new BaseResponseErrorHandler();
    }

    public BulletClientContext(ContentConvertManager contentConvertManager) {
        this();
        this.contentConvertManager = contentConvertManager;
    }

    public void setContentConvertManager(ContentConvertManager contentConvertManager) {
        this.contentConvertManager = contentConvertManager;
    }

    public void setResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;
    }

    public void setHandshakeInfoProvider(HandshakeInfoProvider handshakeInfoProvider) {
        this.handshakeInfoProvider = handshakeInfoProvider;
    }

    public void setClientConfig(BulletClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    protected void setServerConnectionManager(ServerConnectionManager serverConnectionManager) {
        this.serverConnectionManager = serverConnectionManager;
        this.serverConnectionManager.setContext(this);
    }

    public void start() {
        this.serverConnectionManager.startIdleConnectionRecycle();
    }

    public void isReadyCheck() {
        if (this.contentConvertManager == null) {
            throw new ClientContextNotReadyException("Content convert manager is not available.");
        }
        if (this.responseErrorHandler == null) {
            throw new ClientContextNotReadyException("Response error handler is not available.");
        }
        if (this.clientConfig == null) {
            throw new ClientContextNotReadyException("Client config is not available.");
        }
        if (this.serverConnectionManager == null) {
            throw new ClientContextNotReadyException("Server connection manager is not available.");
        }
    }

    public void shutdown() {
        this.serverConnectionManager.shutdown();
    }
}
