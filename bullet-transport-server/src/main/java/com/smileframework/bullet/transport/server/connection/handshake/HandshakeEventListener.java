package com.smileframework.bullet.transport.server.connection.handshake;

import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;
import com.smileframework.bullet.transport.server.BulletServerContext;
import com.smileframework.bullet.transport.server.connection.ClientConnection;

/**
 * 监听连接Bullet协议握手事件
 */
public interface HandshakeEventListener {

    /**
     * 握手通知
     */
    void handshake(BulletRequest<ConnectionHandshake> request, ClientConnection connection, BulletServerContext serverContext);

}
