package com.smileframework.bullet.transport.server.netty.handler;

import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import com.smileframework.bullet.transport.server.connection.ClientConnectionManager;
import com.smileframework.bullet.transport.server.communication.ServerCommunicatedManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Bullet 的原生Netty handler
 * 主要负责数据读取事件分发到 BulletServerCommunicatedManager
 * 将客户端连接事件分发到 ServerChannelManager
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerChannelEventHandler extends SimpleChannelInboundHandler<BulletRequest> {

    /**
     * 连接管理器
     */
    private ClientConnectionManager serverChannelManager;

    /**
     * 通讯管理器
     */
    private ServerCommunicatedManager communicatedManager;


    public ServerChannelEventHandler(ClientConnectionManager serverChannelManager, ServerCommunicatedManager communicatedManager) {
        this.serverChannelManager = serverChannelManager;
        this.communicatedManager = communicatedManager;
    }


    /**
     * 连接失效回调
     * 通过连接管理器，将失效的连接关闭（关闭还会清楚其注册信息）
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            ClientConnection channelWrap = this.serverChannelManager.getClientConnection(ctx);
            if (channelWrap != null) {
                log.info("[Bullet-Transport-Server] registered channel inactive : " + channelWrap);
            } else {
                log.error("[Bullet-Transport-Server] unregistered channel inactive : " + ctx.channel().remoteAddress());
            }
        }
        this.serverChannelManager.disconnect(ctx);
    }

    /**
     * 有新的客户端连接激活
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientConnection channelWrap = this.serverChannelManager.nettyChannelRegistry(ctx);
        if (log.isDebugEnabled()) {
            if (channelWrap != null) {
                log.info("[Bullet-Transport-Server] channel registered : " + channelWrap);
            }
        }
    }

    /**
     * 错误拦截，如果出现任何异常直接断开链接
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ClientConnection channelWrap = this.serverChannelManager.getClientConnection(ctx);
        if (channelWrap != null) {
            log.error("[Bullet-Transport-Server] Bullet transport exception caught with " + channelWrap, cause);
        } else {
            log.error("[Bullet-Transport-Server] Bullet transport exception caught with unregistered channel " + ctx.channel().remoteAddress(), cause);
        }
        this.serverChannelManager.disconnect(ctx);
    }

    /**
     * 事件触发器，目前只有连接空闲心跳检测事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            this.communicatedManager.userEventTriggered(ctx, evt);
        } catch (Exception e) {
            log.error("[Bullet-Transport-Server] Bullet transport userEventTriggered dispatcher error .", e);
        }
    }

    /**
     * 请求数据读取，并交由 BulletServerCommunicatedManager 进行处理分发
     *
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BulletRequest request) throws Exception {
        try {
            this.communicatedManager.requestHandle(ctx, request);
        } catch (Exception e) {
            log.error("[Bullet-Transport-Server] Bullet transport request handler dispatch error .", e);
            this.serverChannelManager.disconnect(ctx);
        }
    }


}
