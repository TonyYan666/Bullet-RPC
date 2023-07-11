package com.smileframework.bullet.transport.server.connection;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;
import com.smileframework.bullet.transport.server.BulletServerContext;
import com.smileframework.bullet.transport.common.exception.transport.server.ClientConnectionManagerException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 连接管理器
 */
@Slf4j
public class ClientConnectionManager {

    /**
     * 客户端ID + 连接 channel warp
     */
    private final Map<String, ClientConnection> connectionIdWithConnectionMap = new ConcurrentHashMap<>();

    /**
     * 连接channel 对应 channel warp
     */
    private final Map<Channel, ClientConnection> channelWithConnectionMap = new ConcurrentHashMap<>();

    /**
     * 服务ID 对应 channel warp 列表
     */
    private final Map<String, Set<ClientConnection>> serviceIdWithConnectionsMap = new ConcurrentHashMap<>();

    /**
     * 实例ID 对应 channel wrap 代表一个实例可以建立多个连接
     */
    private final Map<String, Set<ClientConnection>> instanceIdWithConnectionsMap = new ConcurrentHashMap<>();

    /**
     * 地址 对应 channel wrap
     */
    private final Map<String, ClientConnection> addressWithConnectionsMap = new ConcurrentHashMap<>();

    /**
     * 是否已经停止服务
     */
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    /**
     * Bullet 服务上下文
     */
    private final BulletServerContext serverContext;


    public ClientConnectionManager(BulletServerContext serverContext) {
        this.serverContext = serverContext;
    }

    /**
     * 获得 channel wrap
     * 主要 channel wrap 包含认证是否成功，连接的属性（服务ID、实例ID） 元数据等等
     */
    public ClientConnection getClientConnection(ChannelHandlerContext channelHandlerContext) {
        ClientConnection connection = this.channelWithConnectionMap.get(channelHandlerContext.channel());
        if (connection != null) {
            return connection;
        }
        return this.nettyChannelRegistry(channelHandlerContext);
    }

    /**
     * 连接注册
     */
    public ClientConnection nettyChannelRegistry(ChannelHandlerContext channelHandlerContext) {
        if (isShutdown.get()) {
            throw new ClientConnectionManagerException("Server channel manager has been shouted down.");
        }
        ClientConnection connection = this.channelWithConnectionMap.get(channelHandlerContext.channel());
        if (connection != null) {
            return connection;
        }
        synchronized (channelHandlerContext.channel()) {
            connection = this.channelWithConnectionMap.get(channelHandlerContext.channel());
            if (connection != null) {
                return connection;
            }
            connection = ClientConnection.createBulletClientChannelWrap(channelHandlerContext, this.serverContext);
            this.connectionIdWithConnectionMap.put(connection.getConnectionId(), connection);
            this.channelWithConnectionMap.put(connection.getChannelHandlerContext().channel(), connection);
            this.addressWithConnectionsMap.put(connection.getRemoteAddress(), connection);
        }
        log.info("[Bullet-Server] New client connection has been connected. remote address : " + connection.getRemoteAddress());
        return connection;
    }

    /**
     * 取消连接注册
     */
    private ClientConnection nettyChannelCancelRegistration(ChannelHandlerContext channelHandlerContext) {
        ClientConnection connection = this.channelWithConnectionMap.get(channelHandlerContext.channel());
        if (connection == null) {
            return null;
        }
        return this.nettyChannelCancelRegistration(connection);
    }

    /**
     * 取消连接注册
     *
     * @param connection
     * @return
     */
    private ClientConnection nettyChannelCancelRegistration(ClientConnection connection) {
        ChannelHandlerContext channelHandlerContext = connection.getChannelHandlerContext();
        synchronized (channelHandlerContext.channel()) {
            this.channelWithConnectionMap.remove(channelHandlerContext.channel());
            this.connectionIdWithConnectionMap.remove(connection.getConnectionId());
            this.addressWithConnectionsMap.remove(connection.getRemoteAddress());
            if (StrUtil.isNotEmpty(connection.getServiceId())) {
                Set<ClientConnection> clientConnections = this.serviceIdWithConnectionsMap.get(connection.getServiceId());
                if (clientConnections != null) {
                    clientConnections.remove(connection);
                }
            }
            if (StrUtil.isNotEmpty(connection.getServiceInstanceId())) {
                Set<ClientConnection> clientConnections = this.instanceIdWithConnectionsMap.get(connection.getServiceInstanceId());
                if (clientConnections != null) {
                    clientConnections.remove(connection);
                }
            }
        }
        log.info("[Bullet-Server] " + connection.getRemoteAddress() + " client disconnected. service-id[" + connection.getServiceId() + "] instance-id[" + connection.getServiceInstanceId() + "] ");
        return connection;
    }

    /**
     * 握手信息注册
     * 主要将握手得到的客户端属性绑定到连接的wrap当中
     */
    public ClientConnection handshake(ConnectionHandshake handshake, boolean authenticationResult, ClientConnection connection) {
        connection.handshake(handshake.getServiceId(), handshake.getInstanceId(), authenticationResult);
        log.info("[Bullet-Server] " + connection.getRemoteAddress() + " client connection handshake success. service-id[" + connection.getServiceId() + "] instance-id[" + connection.getServiceInstanceId() + "] authenticationResult[" + connection.getIsAuthentication() + "].");
        Set<ClientConnection> serviceChannelWrapSet = this.getConnectionSetByMap(handshake.getServiceId(), this.serviceIdWithConnectionsMap);
        Set<ClientConnection> instanceChannelWrapSet = this.getConnectionSetByMap(handshake.getInstanceId(), this.instanceIdWithConnectionsMap);
        synchronized (connection.channel()) {
            connection = this.channelWithConnectionMap.get(connection.channel());
            if (connection == null) {
                return null;
            }
            instanceChannelWrapSet.add(connection);
            serviceChannelWrapSet.add(connection);
        }
        return connection;
    }

    /**
     * 安全获得指定KEY的ClientConnection Set
     *
     * @param key
     * @param connectionMap
     * @return
     */
    private Set<ClientConnection> getConnectionSetByMap(String key, Map<String, Set<ClientConnection>> connectionMap) {
        Set<ClientConnection> connectionSet = connectionMap.get(key);
        if (connectionSet == null) {
            synchronized (connectionMap) {
                connectionSet = connectionMap.get(key);
                if (connectionSet == null) {
                    connectionSet = new ConcurrentHashSet<>();
                    if (isShutdown.get()) {
                        throw new ClientConnectionManagerException("Server channel manager has been shouted down.");
                    }
                    connectionMap.put(key, connectionSet);
                }
            }
        }
        return connectionSet;
    }


    /**
     * 断开一个客户端连接
     */
    public void disconnect(ChannelHandlerContext channelHandlerContext) {
        ClientConnection connection = this.nettyChannelCancelRegistration(channelHandlerContext);
        if (connection == null) {
            if (channelHandlerContext.channel().isActive()) {
                channelHandlerContext.disconnect();
            }
            return;
        }
        if (connection.isActive()) {
            try {
                connection.getChannelHandlerContext().disconnect();
            } catch (Exception e) {
                log.warn("[Bullet-Server] " + connection.getRemoteAddress() + " client connection disconnect error.", e);
            }
        }
    }

    /**
     * 断开一个客户端连接
     */
    public void disconnect(ClientConnection connection) {
        this.nettyChannelCancelRegistration(connection);
        if (!connection.channel().isOpen()) {
            return;
        }
        try {
            if (connection != null) {
                connection.syncDisconnect();
            } else {
                connection.getChannelHandlerContext().disconnect().sync();
            }
        } catch (Exception e) {
            log.error("[Bullet Framework] channel disconnect error.", e);
        }
    }

    /**
     * 异步断开链接
     */
    public ChannelFuture asyncDisconnect(ChannelHandlerContext channelHandlerContext) {
        ClientConnection channelWrap = this.nettyChannelCancelRegistration(channelHandlerContext);
        if (!channelHandlerContext.channel().isOpen()) {
            return new DefaultChannelPromise(channelHandlerContext.channel()).setSuccess();
        }
        if (channelWrap != null) {
            return channelWrap.asyncDisconnect();
        } else {
            return channelHandlerContext.disconnect();
        }
    }

    /**
     * 异步断开链接
     */
    public ChannelFuture asyncDisconnect(ClientConnection connection) {
        this.nettyChannelCancelRegistration(connection);
        if (!connection.channel().isOpen()) {
            return new DefaultChannelPromise(connection.channel()).setSuccess();
        }
        return connection.asyncDisconnect();
    }

    /**
     * 关闭连接管理器
     */
    public void shutdown() {
        this.isShutdown.set(true);
        try {
            Collection<ClientConnection> channels = this.channelWithConnectionMap.values();
            if (CollectionUtil.isNotEmpty(channels)) {
                for (ClientConnection channel : channels) {
                    try {
                        channel.syncDisconnect();
                    } catch (Throwable e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
            this.channelWithConnectionMap.clear();
            this.connectionIdWithConnectionMap.clear();
            this.serviceIdWithConnectionsMap.clear();
            this.instanceIdWithConnectionsMap.clear();
            this.addressWithConnectionsMap.clear();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }


}
