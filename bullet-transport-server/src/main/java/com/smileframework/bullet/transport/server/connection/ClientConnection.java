package com.smileframework.bullet.transport.server.connection;

import cn.hutool.core.lang.UUID;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.server.BulletServerContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.NetUtil;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 连接 channel wrap
 */

public class ClientConnection {

    /**
     * channel 上下文
     */
    @Getter
    private ChannelHandlerContext channelHandlerContext;

    /**
     * 连接ID
     */
    @Getter
    private String connectionId;

    /**
     * 连接客户端ip地址
     */
    @Getter
    private String remoteAddress;

    /**
     * 客户端服务ID
     */
    @Getter
    private String serviceId;

    /**
     * 客户端实例ID
     */
    @Getter
    private String serviceInstanceId;

    /**
     * 是否已经握手
     */
    @Getter
    private Boolean hasHandshake = Boolean.FALSE;

    /**
     * 连接是否认证成功
     */
    @Getter
    private Boolean isAuthentication = Boolean.FALSE;

    /**
     * 建立连接的时间
     */
    @Getter
    private Long firstConnectedTime = System.currentTimeMillis();

    /**
     * 连接的元数据（附加自定义属性）
     */
    @Getter
    private Map<String, Object> connectionMeta = new ConcurrentHashMap<>();

    /**
     * 最后通信时间
     */
    @Getter
    private Long lastCommunicateTimestamp = System.currentTimeMillis();

    /**
     * Bullet server 服务上下文
     */
    @Getter
    private final BulletServerContext serverContext;

    /**
     * 创建连接wrap
     *
     * @param channelHandlerContext
     * @return
     */
    public static ClientConnection createBulletClientChannelWrap(ChannelHandlerContext channelHandlerContext, BulletServerContext serverContext) {
        return new ClientConnection(channelHandlerContext, serverContext);
    }


    private ClientConnection(ChannelHandlerContext channelHandlerContext, BulletServerContext serverContext) {
        this.channelHandlerContext = channelHandlerContext;
        this.serverContext = serverContext;
        this.connectionId =
                this.remoteAddress = NetUtil.toSocketAddressString((InetSocketAddress) channelHandlerContext.channel().remoteAddress());
        this.connectionId = UUID.fastUUID().toString(true);
    }

    /**
     * 关闭连接
     *
     * @throws InterruptedException
     */
    protected void syncDisconnect() throws InterruptedException {
        this.channelHandlerContext.channel().close().sync();
    }

    /**
     * 异步关闭连接
     *
     * @return
     */
    protected ChannelFuture asyncDisconnect() {
        return this.channelHandlerContext.channel().close();
    }

    /**
     * 连接是否可用
     *
     * @return
     */
    public boolean isActive() {
        return this.channelHandlerContext.channel().isActive();
    }

    /**
     * 握手熟悉绑定
     *
     * @param serviceId
     * @param instanceId
     * @param isAuthentication
     */
    protected void handshake(String serviceId, String instanceId, boolean isAuthentication) {
        this.serviceId = serviceId;
        this.serviceInstanceId = instanceId;
        this.isAuthentication = isAuthentication;
        this.hasHandshake = true;
    }

    /**
     * 获得NETTY 原生channel
     *
     * @return
     */
    public Channel channel() {
        return this.channelHandlerContext.channel();
    }


    /**
     * 断开链接
     */
    public void disconnect() {
        this.serverContext.disconnect(this);
    }

    /**
     * 发送响应
     */
    public <T> void sendResponse(BulletResponse<T> response) {
        this.serverContext.sendResponse(this, response);
    }

    /**
     * 更新最后一次通讯时间
     */
    public void updateLastCommunicateTimestamp() {
        this.lastCommunicateTimestamp = System.currentTimeMillis();
    }

    /**
     * 添加自定义元数据（自定义属性）
     */
    public <T> void addMeta(String key, T value) {
        this.connectionMeta.put(key, value);
    }

    /**
     * 获得自定义元数据（自定义属性）
     */
    public <T> T getMeta(String key) {
        return (T) this.connectionMeta.get(key);
    }

    /**
     * 获得自定义元数据（自定义属性）当为空时返回默认值
     */
    public <T> T getMeta(String key, T defaultValue) {
        return (T) this.connectionMeta.getOrDefault(key, defaultValue);
    }

    @Override
    public String toString() {
        return "BulletClientChannelWrap{" +
                "channelId='" + connectionId + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", serviceInstanceId='" + serviceInstanceId + '\'' +
                ", firstConnectedTime=" + firstConnectedTime +
                ", lastCommunicateTimestamp=" + lastCommunicateTimestamp +
                '}';
    }
}
