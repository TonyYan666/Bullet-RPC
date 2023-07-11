package com.smileframework.bullet.rpc.consumer;

import com.smileframework.bullet.transport.client.config.BulletClientConfig;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.connection.handshake.impl.DefaultHandshakeInfoProvider;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;

public class DefaultBulletConsumerContextBuilder {

    private BulletConsumerContext context;

    public static DefaultBulletConsumerContextBuilder create() {
        return new DefaultBulletConsumerContextBuilder();
    }

    private DefaultBulletConsumerContextBuilder() {
        this.context = new BulletConsumerContext(new ContentConvertManager());
        context.setClientConfig(new BulletClientConfig());
        context.setServerConnectionManager(new ServerConnectionManager());
        context.setHandshakeInfoProvider(new DefaultHandshakeInfoProvider());
    }

    public BulletConsumerContext build() {
        return this.context;
    }

}
