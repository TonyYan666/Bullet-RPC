package com.smileframework.bullet.transport.server;

import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.common.protocol.serialization.handler.ContentConvertor;
import com.smileframework.bullet.transport.server.communication.ServerCommunicatedManager;
import com.smileframework.bullet.transport.server.authentication.ConnectionAuthenticationManager;
import com.smileframework.bullet.transport.server.authentication.impl.DefaultConnectionAuthenticationManager;
import com.smileframework.bullet.transport.server.communication.handler.ServerCommunicatedHandler;
import com.smileframework.bullet.transport.server.connection.handshake.HandshakeEventListener;
import com.smileframework.bullet.transport.server.connection.handshake.HandshakeHandler;
import com.smileframework.bullet.transport.server.connection.heartbeat.ServerHeartbeatHandler;
import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import com.smileframework.bullet.transport.server.connection.ClientConnectionManager;
import com.smileframework.bullet.transport.server.netty.BulletTransportServer;
import lombok.Getter;
import lombok.Setter;

public class BulletServerContext {

    /**
     * Bullet 服务器配置
     */
    @Getter
    @Setter
    private BulletTransportServerConfig transportServerConfig;

    /**
     * 客户端连接管理器
     */
    @Getter
    private ClientConnectionManager clientConnectionManager;

    /**
     * 内容转换器
     */
    @Getter
    private ContentConvertManager contentConvertManager;

    /**
     * 服务通讯管理器
     */
    @Getter
    private ServerCommunicatedManager serverCommunicatedManager;

    /**
     * 服务
     */
    @Getter
    private BulletTransportServer transportServer;

    /**
     * 连接认证中心
     */
    @Getter
    private ConnectionAuthenticationManager connectionAuthenticationManager;

    /**
     * 握手处理器
     */
    private HandshakeHandler handshakeHandler;

    /**
     * 服务器心跳处理器
     */
    private ServerHeartbeatHandler serverHeartbeatHandler;

    /**
     * 是否已经完成初始化
     */
    @Getter
    private Boolean alreadyInit = false;

    /**
     * 初始化Bullet Server Context
     *
     * @param serverConfig
     */
    protected BulletServerContext init(BulletTransportServerConfig serverConfig) {
        return this.init(serverConfig, new ContentConvertManager());
    }

    protected BulletServerContext init(BulletTransportServerConfig serverConfig, ContentConvertManager contentConvertManager) {
        if (this.alreadyInit) {
            throw new BulletConfigException("Bullet Server context has already initialized.");
        }
        this.transportServerConfig = serverConfig;
        this.connectionAuthenticationManager = new DefaultConnectionAuthenticationManager();
        this.clientConnectionManager = new ClientConnectionManager(this);
        this.contentConvertManager = contentConvertManager;
        this.serverCommunicatedManager = new ServerCommunicatedManager(this.clientConnectionManager, this.contentConvertManager);
        this.transportServer = new BulletTransportServer(this.clientConnectionManager, this.transportServerConfig, this.serverCommunicatedManager);
        this.initServerInfrastructureCommunicatedHandler();
        this.alreadyInit = true;
        return this;
    }

    /**
     * 初始化基础通讯处理器（心跳、握手）
     */
    private void initServerInfrastructureCommunicatedHandler() {
        this.handshakeHandler = new HandshakeHandler(this, this.connectionAuthenticationManager);
        this.serverHeartbeatHandler = new ServerHeartbeatHandler(this);
        this.addServerCommunicatedHandler(this.handshakeHandler);
        this.addServerCommunicatedHandler(this.serverHeartbeatHandler);
    }


    /**
     * 添加通讯处理器（框架内部使用）
     *
     * @param communicatedHandler
     * @return
     */
    protected BulletServerContext addServerCommunicatedHandler(ServerCommunicatedHandler communicatedHandler) {
        this.serverCommunicatedManager.addHandler(communicatedHandler);
        return this;
    }

    /**
     * 添加握手事件监听器
     *
     * @param listeners
     * @return
     */
    public BulletServerContext addHandshakeEventListener(HandshakeEventListener... listeners) {
        this.handshakeHandler.addListener(listeners);
        return this;
    }

    /**
     * 设置客户端连接认证管理器
     *
     * @param authenticationManager
     * @return
     */
    public BulletServerContext setConnectionAuthenticationManager(ConnectionAuthenticationManager authenticationManager) {
        this.connectionAuthenticationManager = authenticationManager;
        this.handshakeHandler.setAuthenticationManager(authenticationManager);
        return this;
    }

    /**
     * 替换Bullet transport 层默认提供的内容转换器
     *
     * @param contentConvertors
     * @return
     */
    public BulletServerContext replaceContentConvertor(ContentConvertor... contentConvertors) {
        this.contentConvertManager.replaceContentConvertors(contentConvertors);
        return this;
    }

    /**
     * 断开链接
     *
     * @param connection
     */
    public void disconnect(ClientConnection connection) {
        this.clientConnectionManager.disconnect(connection);
    }

    /**
     * 发送响应
     *
     * @param connection
     * @param response
     * @param <T>
     */
    public <T> void sendResponse(ClientConnection connection, BulletResponse<T> response) {
        this.serverCommunicatedManager.sendResponse(connection, response);
    }

    /**
     * 启动服务
     */
    public void doOpen() {
        this.transportServer.doOpen();
    }

    /**
     * 关闭服务
     */
    public void doClose() {
        this.transportServer.doClose();
    }

}
