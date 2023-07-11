package com.smileframework.bullet.transport.client.connection.handshake;

import java.util.Map;

/**
 * 握手管理器
 */
public interface HandshakeInfoProvider {

    /**
     * 表明当前节点的 instance id
     */
    String instanceId();

    /**
     * 表明当前节点的 service id
     */
    String serviceId();

    /**
     * 连接的认证令牌
     */
    String authorization();

    /**
     * 握手的附加信息
     */
    Map<String, String> attributes();

}
