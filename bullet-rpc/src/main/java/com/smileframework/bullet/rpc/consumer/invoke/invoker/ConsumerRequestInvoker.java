package com.smileframework.bullet.rpc.consumer.invoke.invoker;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConsumerRequestInvoker {

    /**
     * 执行远程调用
     *
     * @return
     */
    BulletResponse<Object> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);

    /**
     * 异步执行远程调用
     *
     * @param methodDefinition 方法定义
     * @param request         请求对象
     * @param listeners       监听器
     * @return 返回值
     */
    BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners);


    /**
     * 响应式调用
     *
     * @param methodDefinition
     * @param request
     * @return
     */
    Mono<BulletResponse<Object>> reactiveInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);
}