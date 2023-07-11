package com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast;

import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.List;

public interface BroadcastAsyncInvokerCallback<T> {

    T callback(List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners);

}
