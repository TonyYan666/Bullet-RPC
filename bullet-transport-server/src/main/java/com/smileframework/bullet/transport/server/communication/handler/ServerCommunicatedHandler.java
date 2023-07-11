package com.smileframework.bullet.transport.server.communication.handler;


import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.server.connection.ClientConnection;

import java.lang.reflect.Type;

/**
 * Bullet 服务端请求处理器
 */
public interface ServerCommunicatedHandler<T> {

    /**
     * 支持的请求类型
     *
     * @return
     */
    int operationType();

    /**
     * 是否需要响应事件
     *
     * @param connection
     * @param evt
     */
    default void userEventTriggered(ClientConnection connection, Object evt) {
    }

    /**
     * 请求处理
     *
     * @param connection
     * @param request
     */
    void requestHandle(ClientConnection connection, BulletRequest<T> request);

    /**
     * 请求实际类型
     *
     * @param request
     * @return
     */
    Type[] requestTypes(BulletRequest<T> request);

}
