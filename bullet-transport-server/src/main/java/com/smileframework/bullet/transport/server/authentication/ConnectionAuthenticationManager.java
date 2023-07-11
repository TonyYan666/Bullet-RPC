package com.smileframework.bullet.transport.server.authentication;


import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;
import com.smileframework.bullet.transport.server.connection.ClientConnection;

/**
 * 连接认证管理器
 */
public interface ConnectionAuthenticationManager {

    boolean authenticate(ClientConnection connection, BulletRequest<ConnectionHandshake> request);

}
