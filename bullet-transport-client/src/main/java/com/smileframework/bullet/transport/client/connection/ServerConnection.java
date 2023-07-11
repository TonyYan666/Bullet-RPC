package com.smileframework.bullet.transport.client.connection;

import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 通信客户端
 * 通讯客户端 持有 connection
 * connection
 */
@Slf4j
public class ServerConnection extends HeartbeatManageableConnection {

    public ServerConnection(ServerConnectionProperties properties) {
        super(properties);
    }
}
