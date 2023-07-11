package com.smileframework.bullet.rpc.consumer.definition;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.consumer.definition.annotation.ServiceConsumer;
import com.smileframework.bullet.rpc.consumer.definition.annotation.ServiceConsumerMethod;
import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.rpc.consumer.invoke.fallback.ConsumerFallbackHandler;
import com.smileframework.bullet.rpc.consumer.invoke.fallback.factory.FallbackHandlerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response.BroadcastFuture;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response.BroadcastResultList;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerDefinitionException;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerFallbackException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 消费者定义构建器
 */
public class ConsumerDefinitionBuilder {

    private Class interfaceClz;

    private ServiceConsumer consumerAnnotation;

    private ConsumerDefinition consumerDefinition;

    private FallbackHandlerFactory fallbackHandlerFactory;

    public static ConsumerDefinitionBuilder create(Class interfaceClz, FallbackHandlerFactory fallbackHandlerFactory) {
        return new ConsumerDefinitionBuilder(interfaceClz, fallbackHandlerFactory);
    }

    private ConsumerDefinitionBuilder(Class interfaceClz, FallbackHandlerFactory fallbackHandlerFactory) {
        this.interfaceClz = interfaceClz;
        this.fallbackHandlerFactory = fallbackHandlerFactory;
    }

    /**
     * 创建消费者定义
     *
     * @return
     */
    public ConsumerDefinition build() {
        this.createConsumerDefinition();
        return this.consumerDefinition;
    }

    public ConsumerDefinition createConsumerDefinition() {
        this.consumerAnnotation = (ServiceConsumer) this.interfaceClz.getAnnotation(ServiceConsumer.class);
        if (this.consumerAnnotation == null) {
            throw new ConsumerDefinitionException("Bullet consumer interface annotation not found.");
        }
        this.consumerDefinition = new ConsumerDefinition();
        this.consumerDefinition.setConsumerInterface(this.interfaceClz);
        this.consumerDefinition.setServerAddress(URI.create(this.consumerAnnotation.serverAddress()));
        String providerPath = this.consumerAnnotation.providerPath();
        if (StrUtil.isBlank(providerPath)) {
            providerPath = this.interfaceClz.getSimpleName();
        }
        if (providerPath.indexOf("/") != 0) {
            providerPath = "/" + providerPath;
        }
        providerPath = providerPath.replace("/+", "/");
        this.consumerDefinition.setProviderPath(providerPath);
        for (Method declaredMethod : this.interfaceClz.getDeclaredMethods()) {
            ConsumerMethodDefinition functionDefinition = this.createMethodDefinition(declaredMethod);
            this.consumerDefinition.addMethodDefinition(functionDefinition);
        }
        if (this.consumerDefinition.getMethodCount() == 0) {
            throw new ConsumerDefinitionException("Bullet consumer interface definition nothing method.");
        }
        Class fallbackClz = this.consumerAnnotation.fallback();
        if (!fallbackClz.equals(Void.class)) {
            if (!fallbackClz.isAssignableFrom(ConsumerFallbackHandler.class) && !fallbackClz.isAssignableFrom(this.interfaceClz)) {
                throw new ConsumerFallbackException("Consumer fallback handler class is not assignable from ConsumerFallbackHandler or " + this.interfaceClz.getName());
            }
            this.consumerDefinition.setConsumerFallbackHandler(this.fallbackHandlerFactory.getFallbackHandler(fallbackClz));
        }
        return this.consumerDefinition;
    }


    public ConsumerMethodDefinition createMethodDefinition(Method declaredMethod) {
        ServiceConsumerMethod methodAnnotation = declaredMethod.getDeclaredAnnotation(ServiceConsumerMethod.class);
        ConsumerMethodDefinition methodDefinition = new ConsumerMethodDefinition();
        methodDefinition.setMethodName(declaredMethod.getName());
        methodDefinition.setReturnType(declaredMethod.getGenericReturnType());
        methodDefinition.setTransportArgumentsTypes(Boolean.FALSE);
        methodDefinition.setPreferResponseActualType(Boolean.FALSE);
        if (CompletableFuture.class.isAssignableFrom(declaredMethod.getReturnType())
                || BroadcastFuture.class.isAssignableFrom(declaredMethod.getReturnType())) {
            methodDefinition.setAsync(true);
        }
        if (Mono.class.isAssignableFrom(declaredMethod.getReturnType())) {
            methodDefinition.setMonoReactor(true);
        }
        if (methodDefinition.isAsync()
                || methodDefinition.isMonoReactor()
                || BroadcastResultList.class.isAssignableFrom(declaredMethod.getReturnType())) {
            if (!(declaredMethod.getGenericReturnType() instanceof ParameterizedType)) {
                throw new ConsumerDefinitionException("Bullet consumer return type of async method could not be extracted the response type. generic return type must be ParameterizedType.");
            }
            Type[] realTypes = ((ParameterizedType) declaredMethod.getGenericReturnType()).getActualTypeArguments();
            if (realTypes == null || realTypes.length != 1) {
                throw new ConsumerDefinitionException("Bullet consumer return type of async method could not be extracted the response type. actual type arguments more than one.");
            }
            methodDefinition.setReturnType(realTypes[0]);
        }
        methodDefinition.setArgumentsTypes(declaredMethod.getGenericParameterTypes());
        methodDefinition.setMethod(declaredMethod);
        methodDefinition.setRequestTimeout(Duration.ofMillis(this.consumerAnnotation.requestTimeoutMills()));
        methodDefinition.setRequestContentType(this.consumerAnnotation.requestContentType());
        methodDefinition.setRequestMode(RequestMode.UNICAST);
        methodDefinition.setConsumerDefinition(this.consumerDefinition);
        if (methodAnnotation != null) {
            methodDefinition.setNotification(methodAnnotation.isNotification());
            methodDefinition.setRequestTimeout(Duration.ofMillis(methodAnnotation.requestTimeoutMills()));
            methodDefinition.setRequestContentType(methodAnnotation.requestContentType());
            methodDefinition.setRequestMode(methodAnnotation.requestMode());
            methodDefinition.setRetry(methodAnnotation.retry());
            methodDefinition.setRetryIntervalMs(methodAnnotation.retryIntervalMs());
            methodDefinition.setPreferResponseActualType(methodAnnotation.preferResponseActualType());
            methodDefinition.setTransportArgumentsTypes(methodAnnotation.transportArgumentsTypes());
            if (StrUtil.isNotBlank(methodAnnotation.name())) {
                methodDefinition.setMethodName(methodAnnotation.name().trim());
            }
        }
        Class methodReturnType = declaredMethod.getReturnType();
        if (methodDefinition.getRequestMode().equals(RequestMode.BROADCAST)) {
            if (!BroadcastFuture.class.isAssignableFrom(methodReturnType)
                    && !BroadcastResultList.class.isAssignableFrom(methodReturnType)) {
                throw new ConsumerDefinitionException("Bullet consumer method definition must be return BroadcastFuture or BroadcastResultList in broadcast request mode.");
            }
        } else {
            if (BroadcastFuture.class.isAssignableFrom(methodReturnType)
                    || BroadcastResultList.class.isAssignableFrom(methodReturnType)) {
                throw new ConsumerDefinitionException("Bullet consumer method definition must not be return BroadcastFuture or BroadcastResultList in unicast request mode.");
            }
        }
        return methodDefinition;
    }

}
