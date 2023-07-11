package com.smileframework.bullet.transport.server.communication;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.smileframework.bullet.transport.common.exception.rpc.serialization.BulletContentConvertException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import com.smileframework.bullet.transport.server.connection.ClientConnectionManager;
import com.smileframework.bullet.transport.server.communication.handler.ServerCommunicatedHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务通信管理器
 */
@Slf4j
public class ServerCommunicatedManager {

    /**
     * 连接管理器
     */
    private final ClientConnectionManager clientConnectionManager;

    /**
     * 内容转换器
     */
    private final ContentConvertManager contentConvertManager;

    /**
     * ServerCommunicatedHandler 主要根据 协议头中的 请求类型 将实际的处理交由不同的 Handler 中处理；
     */
    private final Map<Integer, Set<ServerCommunicatedHandler>> handlerMap = new HashMap<>();


    public ServerCommunicatedManager(ClientConnectionManager clientConnectionManager, ContentConvertManager contentConvertManager) {
        this.clientConnectionManager = clientConnectionManager;
        this.contentConvertManager = contentConvertManager;
    }

    /**
     * 添加处理器
     */
    public void addHandler(ServerCommunicatedHandler... communicatedHandlers) {
        if (communicatedHandlers == null || communicatedHandlers.length <= 0) {
            return;
        }
        for (ServerCommunicatedHandler communicatedHandler : communicatedHandlers) {
            Set<ServerCommunicatedHandler> handlers = this.handlerMap.get(communicatedHandler.operationType());
            if (handlers == null) {
                handlers = new ConcurrentHashSet<>();
                this.handlerMap.put(communicatedHandler.operationType(), handlers);
            }
            handlers.add(communicatedHandler);
        }
    }

    /**
     * 获得事件触发，这里需要分发到各个handler当中处理
     */
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        ClientConnection channelWrap = this.clientConnectionManager.getClientConnection(ctx);
        if (channelWrap == null) {
            this.clientConnectionManager.disconnect(ctx);
            return;
        }
        for (Set<ServerCommunicatedHandler> handlers : handlerMap.values()) {
            for (ServerCommunicatedHandler handler : handlers) {
                try {
                    handler.userEventTriggered(channelWrap, evt);
                } catch (Throwable e) {
                    log.error("[Bullet-Transport-Server] ServerCommunicatedManager user event triggered handler " + handler.getClass().toString() + " throw an exception.", e);
                }
            }
        }
    }

    /**
     * 请求分发 按照不同处理器支持的类型进行分发
     */
    public void requestHandle(ChannelHandlerContext ctx, BulletRequest<byte[]> request) {
        ClientConnection connection = this.clientConnectionManager.getClientConnection(ctx);
        if (connection == null) {
            this.clientConnectionManager.disconnect(ctx);
            return;
        }
        Set<ServerCommunicatedHandler> handlers = this.handlerMap.get(request.getHeader().getOperationType());
        if (CollectionUtil.isEmpty(handlers)) {
            this.sendOperationTypeUnsupportedResponse(connection, request);
            return;
        }
        for (ServerCommunicatedHandler handler : handlers) {
            try {
                Type[] types = handler.requestTypes(request);
                BulletRequest requestAfterContentConvert = this.contentConvertManager.payloadBytesToObject(request, types);
                handler.requestHandle(connection, requestAfterContentConvert);
            } catch (Throwable e) {
                log.error("[Bullet-Transport-Server] BulletServerCommunicatedManager request handle error, handler " + handler.getClass().toString() + ".", e);
                this.sendRequestHandleErrorResponse(connection, request, e);
            }
        }
    }

    /**
     * 发送请求类型不支持的错误响应
     */
    private void sendOperationTypeUnsupportedResponse(ClientConnection connection, BulletRequest<byte[]> request) {
        BulletResponse<Void> response = BulletResponse.createUnsupportedOperationResponse(request);
        this.sendResponse(connection, response);
    }

    /**
     * 发送处理请求异常的响应、
     * （正常由处理器自身返回异常错误响应，但是如果处理器报异常将由通讯管理器托管）
     */
    private void sendRequestHandleErrorResponse(ClientConnection connection, BulletRequest<byte[]> request, Throwable e) {
        BulletResponse<Void> response = BulletResponse.createRequestHandleErrorResponse(request, e);
        if (e.getClass().isAssignableFrom(BulletContentConvertException.class)) {
            response.getHeader().setCode(BulletResponseCode.CONTENT_TYPE_CONVERT_FAILURE);
        }
        this.sendResponse(connection, response);
    }

    /**
     * 响应客户端请求
     *
     * @param connection
     * @param response
     * @param <T>
     */
    public <T> void sendResponse(ClientConnection connection, BulletResponse<T> response) {
        BulletResponse<byte[]> result = this.contentConvertManager.responseObjectToBytes(response);
        connection.getChannelHandlerContext().writeAndFlush(result);
    }

}
