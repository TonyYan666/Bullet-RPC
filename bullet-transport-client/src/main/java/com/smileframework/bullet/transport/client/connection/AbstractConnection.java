package com.smileframework.bullet.transport.client.connection;

import com.smileframework.bullet.transport.client.connection.lifecycle.BulletTransportClientLifecycle;
import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import com.smileframework.bullet.transport.client.netty.NettyClient;
import com.smileframework.bullet.transport.common.exception.connection.CheckServerConnectionException;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientConnectException;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractConnection extends SimpleChannelInboundHandler<BulletResponse<byte[]>> {

    @Getter
    protected ServerConnectionProperties properties;

    /**
     * 连接对象
     */
    @Getter
    protected NettyClient nettyClient;

    /**
     * 是否连接成功
     */
    protected AtomicBoolean connected = new AtomicBoolean(false);

    /**
     * 连接声明周期托管（将建立连接和关闭连接都对外通知）
     */
    @Setter
    protected BulletTransportClientLifecycle lifecycle;

    /**
     * 上一次通讯的时间
     */
    protected AtomicLong lastCommunicatedTime = new AtomicLong(System.currentTimeMillis());

    /**
     * 连接自定义属性
     */
    private Map<String, Object> attributes = new ConcurrentHashMap<>();


    public AbstractConnection(ServerConnectionProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取属性
     *
     * @param attributeName
     * @param <T>
     * @return
     */
    public <T> T getAttribute(String attributeName) {
        return (T) this.attributes.get(attributeName);
    }

    /**
     * 设置属性
     *
     * @param attributeName
     * @param attributeObject
     */
    public void setAttribute(String attributeName, Object attributeObject) {
        this.attributes.put(attributeName, attributeObject);
    }


    /**
     * 连接失效
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.disconnect();
    }

    /**
     * 创建连接
     */
    public void connect() {
        this.checkBeforeConnect();
        if (this.nettyClient != null) {
            this.disconnect();
        }
        this.nettyClient = new NettyClient(this.properties, this);
        try {
            this.nettyClient.openConnection();
            connected.set(true);
            if (this.lifecycle != null) {
                this.lifecycle.connected(this);
            }
        } catch (Exception e) {
            throw new BulletClientConnectException("connect server failure.", e);
        }
    }

    /**
     * 断开链接
     */
    public void disconnect() {
        try {
            if (this.nettyClient != null) {
                this.nettyClient.disconnect();
                this.nettyClient = null;
                this.connected.set(false);
                if (this.lifecycle != null) {
                    this.lifecycle.disconnected(this);
                }
                this.nettyClient = null;
            }
        } catch (Exception e) {
            throw new BulletClientConnectException("disconnect server failure", e);
        }
    }

    /**
     * 更新最后通讯时间
     */
    protected void updateLastCommunicatedTime() {
        this.lastCommunicatedTime.set(System.currentTimeMillis());
    }

    /**
     * 获得最后通讯时间
     */
    public Long getLastCommunicatedTime() {
        return this.lastCommunicatedTime.get();
    }


    /**
     * 是否连接
     *
     * @return
     */
    public boolean isConnected() {
        return this.connected.get();
    }

    protected void checkBeforeConnect() {
        if (this.properties == null) {
            throw new CheckServerConnectionException("server connection properties didn't set.");
        }
    }


}
