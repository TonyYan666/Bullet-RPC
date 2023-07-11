package com.smileframework.bullet.transport.server.authentication.impl;

import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;
import com.smileframework.bullet.transport.server.authentication.ConnectionAuthenticationManager;
import com.smileframework.bullet.transport.server.connection.ClientConnection;

public class DefaultConnectionAuthenticationManager implements ConnectionAuthenticationManager {

    @Override
    public boolean authenticate(ClientConnection connection, BulletRequest<ConnectionHandshake> request) {
        return true;
    }
}
