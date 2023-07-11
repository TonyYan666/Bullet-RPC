package com.smileframework.bullet.transport.server.connection.handshake;

import com.smileframework.bullet.transport.common.exception.authentication.BulletChannelAuthenticationError;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import com.smileframework.bullet.transport.server.BulletServerContext;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import com.smileframework.bullet.transport.server.authentication.ConnectionAuthenticationManager;
import com.smileframework.bullet.transport.server.communication.handler.ServerCommunicatedHandler;
import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 连接握手请求处理器
 * 握手请求是对连接的身份进行识别
 * 服务ID 实例ID 节点认证信息
 */
@Slf4j
public class HandshakeHandler implements ServerCommunicatedHandler<ConnectionHandshake> {

    private BulletServerContext serverContext;

    private List<HandshakeEventListener> listeners = new CopyOnWriteArrayList<>();

    @Setter
    private ConnectionAuthenticationManager authenticationManager;

    public HandshakeHandler(BulletServerContext serverContext, ConnectionAuthenticationManager authenticationManager) {
        this.serverContext = serverContext;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 添加握手监听器
     *
     * @param eventListeners
     */
    public void addListener(HandshakeEventListener... eventListeners) {
        for (HandshakeEventListener eventListener : eventListeners) {
            this.listeners.add(eventListener);
        }
    }

    /**
     * 通知握手事件
     *
     * @param request
     * @param connection
     */
    private void handshakeEventNotify(BulletRequest<ConnectionHandshake> request, ClientConnection connection) {
        for (HandshakeEventListener listener : this.listeners) {
            try {
                listener.handshake(request, connection, this.serverContext);
            } catch (Exception e) {
                log.error("[Bullet-Transport-Server] handshake listener  " + listener.getClass().getName() + " throw an error.", e);
            }
        }
    }


    @Override
    public int operationType() {
        return BulletRequestHeader.OPERATION_TYPE_HANDSHAKE;
    }

    /**
     * 处理握手协议
     */
    @Override
    public void requestHandle(ClientConnection connection, BulletRequest<ConnectionHandshake> request) {
        Boolean isAuthenticated = false;
        //如果不启用认证则不需要做连接认证
        BulletTransportServerConfig serverConfig = this.serverContext.getTransportServerConfig();
        if (!serverConfig.getEnableTransportAuthentication()) {
            this.handshakeSuccess(request, true, connection);
            return;
        }
        ConnectionAuthenticationManager authenticationManager = this.serverContext.getConnectionAuthenticationManager();
        isAuthenticated = authenticationManager.authenticate(connection, request);
        //不接受认证不成功的连接，就直接关闭。否则还算握手成功，只是认证失败而已；
        if (serverConfig.getShutdownUnauthenticatedConnection() && !isAuthenticated) {
            BulletResponse<Void> response = BulletResponse.createErrorResponse(request, BulletResponseCode.INVOKE_ERROR, BulletChannelAuthenticationError.class, "channel authentication failure.");
            serverContext.sendResponse(connection, response);
            this.serverContext.disconnect(connection);
            return;
        }
        this.handshakeSuccess(request, true, connection);
    }

    @Override
    public Type[] requestTypes(BulletRequest<ConnectionHandshake> request) {
        return new Type[]{ConnectionHandshake.class};
    }


    /**
     * 握手成功的回调
     */
    public void handshakeSuccess(BulletRequest<ConnectionHandshake> request, boolean isAuthenticated, ClientConnection connection) {
        this.serverContext.getClientConnectionManager().handshake(request.getPayload(), isAuthenticated, connection);
        this.handshakeEventNotify(request, connection);
        BulletResponse<Void> response = BulletResponse.createSuccessResponse(request, null);
        this.serverContext.sendResponse(connection, response);
    }

}
