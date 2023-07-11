package com.smileframework.bullet.transport.client.connection.future;

import com.smileframework.bullet.transport.common.netty.loop.NettyEventLoopFactory;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import lombok.Getter;

import java.lang.reflect.Type;

/**
 * 请求响应future
 */
@Getter
public class BulletResponseFuture<PAYLOAD, RESPONSE> extends DefaultPromise<BulletResponse<RESPONSE>> {

    private final BulletRequest request;

    private final ResponseTypesProvider responseTypesProvider;

    public static final EventExecutor executor = NettyEventLoopFactory.eventExecutor("BULLET_RESPONSE_FUTURE_EXECUTOR");

    public BulletResponseFuture(BulletRequest<PAYLOAD> request, ResponseTypesProvider responseTypesProvider) {
        super(executor);
        this.request = request;
        this.responseTypesProvider = responseTypesProvider;
    }

    public BulletResponseFuture(BulletRequest<PAYLOAD> request, Type... responseTypes) {
        super(executor);
        this.request = request;
        this.responseTypesProvider = new DefaultResponseTypesProvider(responseTypes);
    }

    public String getRequestId() {
        return this.request.getHeader().getRequestId();
    }

}
