package com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerFallbackException;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.fallback.ConsumerFallbackHandler;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

public class FallbackRequestInvoker implements ConsumerRequestInvoker {

    private ConsumerRequestInvoker requestInvoker;

    public FallbackRequestInvoker(ConsumerRequestInvoker requestInvoker) {
        this.requestInvoker = requestInvoker;
    }

    @Override
    public BulletResponse<Object> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        Object fallbackHandler = methodDefinition.getConsumerDefinition().getConsumerFallbackHandler();
        try {
            return this.requestInvoker.invoke(methodDefinition, request);
        } catch (Exception e) {
            if (fallbackHandler instanceof ConsumerFallbackHandler) {
                ConsumerFallbackHandler consumerFallbackHandler = (ConsumerFallbackHandler) fallbackHandler;
                Object t = consumerFallbackHandler.fallback(methodDefinition, request.getPayload(), e);
                BulletResponse<Object> bulletResponse = BulletResponse.createSuccessResponse(request, t);
                return bulletResponse;
            } else if (fallbackHandler.getClass().isAssignableFrom(methodDefinition.getConsumerDefinition().getConsumerInterface())) {
                try {
                    Object t = methodDefinition.getMethod().invoke(fallbackHandler, request.getPayload());
                    BulletResponse<Object> bulletResponse = BulletResponse.createSuccessResponse(request, t);
                    return bulletResponse;
                } catch (IllegalAccessException illegalAccessException) {
                    throw new ConsumerFallbackException(illegalAccessException);
                } catch (InvocationTargetException invocationTargetException) {
                    throw new ConsumerFallbackException(invocationTargetException);
                }
            } else {
                throw e;
            }
        }
    }

    @Override
    public BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners) {
        Object fallbackHandler = methodDefinition.getConsumerDefinition().getConsumerFallbackHandler();
        final BulletResponseFuture<?, Object> responseFutureProxy = new BulletResponseFuture<>(request, new Type[]{methodDefinition.getReturnType()});
        if (CollectionUtil.isNotEmpty(listeners)) {
            for (GenericFutureListener<Future<? super BulletResponse<Object>>> listener : listeners) {
                responseFutureProxy.addListener(listener);
            }
        }
        GenericFutureListener fallbackListener = internalFuture -> {
            try {
                BulletResponse<Object> response = (BulletResponse<Object>) internalFuture.get();
                responseFutureProxy.setSuccess(response);
            } catch (Exception e) {
                if (fallbackHandler instanceof ConsumerFallbackHandler) {
                    ConsumerFallbackHandler consumerFallbackHandler = (ConsumerFallbackHandler) fallbackHandler;
                    try {
                        Object t = consumerFallbackHandler.fallback(methodDefinition, request.getPayload(), e);
                        BulletResponse<Object> bulletResponse = BulletResponse.createSuccessResponse(request, t);
                        responseFutureProxy.setSuccess(bulletResponse);
                    } catch (Exception fallbackException) {
                        responseFutureProxy.setFailure(fallbackException);
                        return;
                    }
                } else if (fallbackHandler.getClass().isAssignableFrom(methodDefinition.getConsumerDefinition().getConsumerInterface())) {
                    try {
                        Object t = methodDefinition.getMethod().invoke(fallbackHandler, request.getPayload());
                        BulletResponse<Object> bulletResponse = BulletResponse.createSuccessResponse(request, t);
                        responseFutureProxy.setSuccess(bulletResponse);
                    } catch (IllegalAccessException illegalAccessException) {
                        responseFutureProxy.setFailure(new ConsumerFallbackException(illegalAccessException));
                    } catch (InvocationTargetException invocationTargetException) {
                        responseFutureProxy.setFailure(new ConsumerFallbackException(invocationTargetException));
                    }
                } else {
                    responseFutureProxy.setFailure(e);
                }
            }
        };
        this.requestInvoker.asyncInvoke(methodDefinition, request, Lists.newArrayList(fallbackListener));
        return responseFutureProxy;
    }

    @Override
    public Mono<BulletResponse<Object>> reactiveInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return this.requestInvoker.reactiveInvoke(methodDefinition, request)
                .onErrorResume(e -> {
                    Object fallbackHandler = methodDefinition.getConsumerDefinition().getConsumerFallbackHandler();
                    if (fallbackHandler == null) {
                        return false;
                    }
                    if (fallbackHandler instanceof ConsumerFallbackHandler) {
                        return true;
                    }
                    if (fallbackHandler.getClass().isAssignableFrom(methodDefinition.getConsumerDefinition().getConsumerInterface())) {
                        return true;
                    }
                    return false;
                }, (e) -> {
                    Object fallbackHandler = methodDefinition.getConsumerDefinition().getConsumerFallbackHandler();
                    if (fallbackHandler instanceof ConsumerFallbackHandler) {
                        ConsumerFallbackHandler consumerFallbackHandler = (ConsumerFallbackHandler) fallbackHandler;
                        Object t = consumerFallbackHandler.fallback(methodDefinition, request.getPayload(), (Exception) e);
                        BulletResponse<Object> bulletResponse = BulletResponse.createSuccessResponse(request, t);
                        return Mono.just(bulletResponse);
                    }
                    try {
                        Object t = methodDefinition.getMethod().invoke(fallbackHandler, request.getPayload());
                        BulletResponse<Object> bulletResponse = BulletResponse.createSuccessResponse(request, t);
                        return Mono.just(bulletResponse);
                    } catch (IllegalAccessException illegalAccessException) {
                        return Mono.error(new ConsumerFallbackException(illegalAccessException));
                    } catch (InvocationTargetException invocationTargetException) {
                        return Mono.error(new ConsumerFallbackException(invocationTargetException));
                    }
                });
    }

}
