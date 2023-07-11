package com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.util.Map;

/**
 * 广播请求
 */
public interface BroadcastRequestInvoker {

    /**
     * 执行远程调用
     *
     * @return
     */
    Map<String, BulletResponse> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);

    /**
     * 异步执行远程调用
     *
     * @param methodDefinition
     * @param request
     * @param <T>
     * @return
     */
    <T> Map<String, T> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BroadcastAsyncInvokerCallback<T> callback);

}
