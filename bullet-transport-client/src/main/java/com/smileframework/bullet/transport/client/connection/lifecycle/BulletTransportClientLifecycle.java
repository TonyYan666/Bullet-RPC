package com.smileframework.bullet.transport.client.connection.lifecycle;

import com.smileframework.bullet.transport.client.connection.AbstractConnection;

/**
 * 连接生命周期回调
 * 主要在客户端的连接管理器当中，进行对连接注册进行解绑
 */
public interface BulletTransportClientLifecycle {

    /**
     * 连接建立成功
     *
     * @param client
     */
    void connected(AbstractConnection client);

    /**
     * 连接关闭
     *
     * @param client
     */
    void disconnected(AbstractConnection client);

}
