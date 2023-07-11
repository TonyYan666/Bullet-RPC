package com.smileframework.bullet.transport.server.connection.heartbeat;

import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import com.smileframework.bullet.transport.common.protocol.header.BulletResponseHeader;
import com.smileframework.bullet.transport.server.BulletServerContext;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import com.smileframework.bullet.transport.server.communication.handler.ServerCommunicatedHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * 心跳处理器
 */
@Slf4j
public class ServerHeartbeatHandler implements ServerCommunicatedHandler {

    private BulletServerContext serverContext;


    public ServerHeartbeatHandler(BulletServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public int operationType() {
        return BulletRequestHeader.OPERATION_TYPE_HEARTBEAT;
    }

    /**
     * 触发心跳时间，如果空闲没有任何心跳响应，就直接关闭连接
     */
    @Override
    public void userEventTriggered(ClientConnection connection, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
            log.info("[Bullet-Transport-Server] channel idle time out, it is going to disconnect. " + connection);
            this.serverContext.disconnect(connection);
        }
    }

    /**
     * 心跳请求处理
     */
    @Override
    public void requestHandle(ClientConnection connection, BulletRequest request) {
        try {
            log.info("received heartbeat request ... ");
            BulletResponseHeader header = BulletResponseHeader.createByRequestHeader(request.getHeader());
            header.setCode(BulletResponseCode.SUCCESS);
            BulletResponse response = new BulletResponse();
            response.setHeader(header);
            this.serverContext.sendResponse(connection, response);
        } catch (Exception e) {
            this.serverContext.disconnect(connection);
        }
    }

    @Override
    public Type[] requestTypes(BulletRequest request) {
        return new Type[0];
    }
}
