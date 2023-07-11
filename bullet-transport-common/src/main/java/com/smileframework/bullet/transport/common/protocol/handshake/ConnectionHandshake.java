package com.smileframework.bullet.transport.common.protocol.handshake;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接握手消息体
 */
@Getter
@Setter
public class ConnectionHandshake {

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 服务实例认证
     */
    private String authorization;

    /**
     * 额外携带的属性，允许用户在握手过程中，添加额外的处理
     */
    private Map<String, String> attributes = new HashMap<>();


}
