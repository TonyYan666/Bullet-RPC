package com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 失败重试请求调用器装饰者
 */
@Slf4j
public class RetryRequestInvoker implements ConsumerRequestInvoker {

    private final ConsumerRequestInvoker consumerRequestInvoker;

    protected RetryRequestInvoker(ConsumerRequestInvoker consumerRequestInvoker) {
        this.consumerRequestInvoker = consumerRequestInvoker;
    }

    @Override
    public BulletResponse<Object> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        int maxInvokeCount = methodDefinition.getRetry() + 1;
        BulletResponse<Object> response = null;
        for (int i = 0; i < maxInvokeCount; i++) {
            try {
                response = this.consumerRequestInvoker.invoke(methodDefinition, request);
            } catch (Exception e) {
                if (i < maxInvokeCount - 1) {
                    log.info("Bullet consumer request failure, retry later. request -> " + request, e);
                } else {
                    throw e;
                }
            }
            if (response.getHeader().getCode().equals(BulletResponseCode.SUCCESS)) {
                return response;
            } else if (i < maxInvokeCount - 1) {
                log.info("Bullet consumer request failure, retry later. request -> " + request + ". response ->" + response);
            }
        }
        return response;
    }

    @Override
    public BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners) {
        return this.consumerRequestInvoker.asyncInvoke(methodDefinition, request, listeners);
    }

    @Override
    public Mono<BulletResponse<Object>> reactiveInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return this.consumerRequestInvoker.reactiveInvoke(methodDefinition, request).retry(3);
    }

}
