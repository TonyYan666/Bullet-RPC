package com.smileframework.bullet.transport.client.connection;

import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientConnectException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartbeatManageableConnection extends HandshakeConnection {


    public HeartbeatManageableConnection(ServerConnectionProperties properties) {
        super(properties);
    }

    /**
     * 触发器 主要是触发当前连接是否已经没有通讯过
     * 如果没有通讯过则发送心跳请求进行保活
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
        }
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        if (idleStateEvent.state().equals(IdleState.WRITER_IDLE)) {
            try {
                asyncSendHeartbeat();
            } catch (Exception e) {
                log.info("");
            }
        }
        if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
            this.disconnect();
        }
    }

    /**
     * 发送心跳（会等待响应）
     */
    public void sendHeartbeat() {
        BulletRequest<Void> heartbeatRequest = BulletRequest.createHeartbeatRequest();
        BulletResponseFuture<Void, Void> responseFuture = this.sendRequest(heartbeatRequest);
        try {
            BulletResponse<Void> response = responseFuture.get(this.properties.getHeartbeatTimeout().getSeconds(), TimeUnit.SECONDS);
            if (!BulletResponseCode.SUCCESS.equals(response.getHeader().getCode())) {
                throw new BulletClientConnectException("Heart beat response failure. code -> "
                        + response.getHeader().getCode() + ", reason -> " + response.getHeader().getErrorReason());
            }
        } catch (Exception e) {
            log.warn("[Bullet Framework] Bullet transport client heartbeat timeout.", e);
        }
    }

    /**
     * 异步心跳请求
     *
     * @return
     */
    public BulletResponseFuture asyncSendHeartbeat() {
        BulletRequest heartbeatRequest = BulletRequest.createHeartbeatRequest();
        BulletResponseFuture responseFuture = this.sendRequest(heartbeatRequest);
        return responseFuture;
    }



}
