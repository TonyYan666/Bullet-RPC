package com.smileframework.bullet.rpc.consumer.invoke;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerDefinition;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerFilterManager;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvokerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response.BroadcastFuture;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response.BroadcastResultList;
import com.smileframework.bullet.rpc.utils.ActionUrlUtils;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerDefinitionException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;
import io.netty.util.concurrent.GenericFutureListener;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DynamicConsumerInvoker {

    private ServerConnectionManager serverConnectionManager;

    private ConsumerRequestInvokerFactory requestInvokerFactory;

    private BroadcastRequestInvokerFactory broadcastRequestInvokerFactory;

    private ResponseErrorHandler responseErrorHandler;

    private ConsumerFilterManager consumerFilterManager;

    /**
     * 返回类型
     */
    private Type returnType;

    /**
     * 参数类型
     */
    private Type[] argumentsTypes;

    /**
     * 方法名词
     */
    private String methodName;

    /**
     * 是否为通知模式
     */
    private boolean notification = false;


    /**
     * 请求内容类型
     */
    private int requestContentType = BulletContentType.JSON;

    /**
     * 请求超时时间
     */
    private Duration requestTimeout = Duration.ofSeconds(3);

    /**
     * 请求模式
     */
    private RequestMode requestMode = RequestMode.UNICAST;

    /**
     * 是否重试
     */
    private int retry = 0;

    /**
     * 重试间隔时间（毫秒）
     */
    private int retryIntervalMs = 20;

    /**
     * 服务提供者路径
     */
    private String providerPath;

    /**
     * 方法路径
     */
    private String methodPath;

    /**
     * 服务提供者地址
     */
    private String serverAddress;


    /**
     * 优先返回值的实际类型（来自返回头部convertDesc）
     * 如果头部没有 convertDesc 则依然使用方法定义的类型
     * 使用实际类型，需要输出输入的类必须一致包括包路径，如果类不存在 依然会使用参数中的类型
     * 默认情况为false
     */
    private Boolean preferResponseActualType = Boolean.FALSE;


    /**
     * 是否携带实际请求参数结果类型 默认情况下为false
     */
    private Boolean transportArgumentsTypes = Boolean.FALSE;


    public DynamicConsumerInvoker setServerConnectionManager(ServerConnectionManager serverConnectionManager) {
        this.serverConnectionManager = serverConnectionManager;
        return this;
    }

    public DynamicConsumerInvoker setRequestInvokerFactory(ConsumerRequestInvokerFactory requestInvokerFactory) {
        this.requestInvokerFactory = requestInvokerFactory;
        return this;
    }

    public DynamicConsumerInvoker setResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;
        return this;
    }

    public DynamicConsumerInvoker setConsumerFilterManager(ConsumerFilterManager consumerFilterManager) {
        this.consumerFilterManager = consumerFilterManager;
        return this;
    }

    public DynamicConsumerInvoker setBroadcastRequestInvokerFactory(BroadcastRequestInvokerFactory broadcastRequestInvokerFactory) {
        this.broadcastRequestInvokerFactory = broadcastRequestInvokerFactory;
        return this;
    }

    public DynamicConsumerInvoker setReturnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }

    public DynamicConsumerInvoker setArgumentsTypes(Type[] argumentsTypes) {
        this.argumentsTypes = argumentsTypes;
        return this;
    }

    public DynamicConsumerInvoker setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public DynamicConsumerInvoker setNotification(boolean notification) {
        this.notification = notification;
        return this;
    }

    public DynamicConsumerInvoker setRequestContentType(int requestContentType) {
        this.requestContentType = requestContentType;
        return this;
    }

    public DynamicConsumerInvoker setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public DynamicConsumerInvoker setRequestMode(RequestMode requestMode) {
        this.requestMode = requestMode;
        return this;
    }

    public DynamicConsumerInvoker setRetry(int retry) {
        this.retry = retry;
        return this;
    }

    public DynamicConsumerInvoker setRetryIntervalMs(int retryIntervalMs) {
        this.retryIntervalMs = retryIntervalMs;
        return this;
    }

    public DynamicConsumerInvoker setPath(String providerPath, String methodPath) {
        this.providerPath = providerPath;
        this.methodPath = methodPath;
        return this;
    }

    public DynamicConsumerInvoker setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }

    public DynamicConsumerInvoker setPreferResponseActualType(Boolean preferResponseActualType) {
        this.preferResponseActualType = preferResponseActualType;
        return this;
    }

    public DynamicConsumerInvoker setTransportArgumentsTypes(Boolean transportArgumentsTypes) {
        this.transportArgumentsTypes = transportArgumentsTypes;
        return this;
    }

    public <T> T invoke(Object... objects) {
        ConsumerMethodDefinition methodDefinition = this.createVirtualMethodDefinition(objects);
        return (T) functionInvoke(methodDefinition, objects);
    }

    public <T> Mono<T> reactiveInvoke(Object... objects) {
        ConsumerMethodDefinition methodDefinition = this.createVirtualMethodDefinition(objects);
        return this.reactiveMonoFunctionInvoke(methodDefinition, objects).map(o -> (T) o);
    }

    public <T> Future<T> asyncInvoke(Object... objects) {
        ConsumerMethodDefinition methodDefinition = this.createVirtualMethodDefinition(objects);
        return (Future<T>) this.asyncFunctionInvoke(methodDefinition, objects);
    }

    private Object functionInvoke(ConsumerMethodDefinition methodDefinition, Object... objects) {
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        BulletRequest<Object[]> request = this.createRequest(methodDefinition, objects);
        ConsumerRequestInvoker requestInvoker = this.requestInvokerFactory.getRequestInvoker(methodDefinition, request,
                this.serverConnectionManager, this.consumerFilterManager, this.responseErrorHandler);
        if (methodDefinition.getRequestMode().equals(RequestMode.UNICAST)) {
            BulletResponse response = requestInvoker.invoke(methodDefinition, request);
            this.responseErrorHandler.errorHandle(response);
            return response.getResponse();
        }
        BroadcastRequestInvoker broadcastRequestInvoker = this.broadcastRequestInvokerFactory.getRequestInvoker(methodDefinition, request, requestInvoker);
        Map<String, BulletResponse> responseMap = broadcastRequestInvoker.invoke(methodDefinition, request);
        BroadcastResultList resultList = new BroadcastResultList();
        for (BulletResponse response : responseMap.values()) {
            try {
                this.responseErrorHandler.errorHandle(response);
                resultList.add(response.getResponse());
            } catch (RuntimeException e) {
                resultList.add(e);
            }
        }
        return resultList;
    }

    private Mono<Object> reactiveMonoFunctionInvoke(ConsumerMethodDefinition methodDefinition, Object... objects) {
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        if (methodDefinition.getRequestMode().equals(RequestMode.BROADCAST)) {
            throw new ConsumerDefinitionException("Reactive invoke haven't supported broadcast request mode yet.");
        }
        BulletRequest<Object[]> request = this.createRequest(methodDefinition, objects);
        ConsumerRequestInvoker requestInvoker = this.requestInvokerFactory.getRequestInvoker(methodDefinition, request,
                this.serverConnectionManager, this.consumerFilterManager, this.responseErrorHandler);
        return requestInvoker.reactiveInvoke(methodDefinition, request).flatMap(resp -> {
            try {
                this.responseErrorHandler.errorHandle(resp);
                if (resp.getResponse() == null) {
                    return Mono.empty();
                }
                return Mono.just(resp.getResponse());
            } catch (RuntimeException e) {
                return Mono.error(e);
            }
        });
    }

    private Future<Object> asyncFunctionInvoke(ConsumerMethodDefinition methodDefinition, Object... objects) {
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        BulletRequest<Object[]> request = this.createRequest(methodDefinition, objects);
        ConsumerRequestInvoker requestInvoker = this.requestInvokerFactory.getRequestInvoker(methodDefinition, request,
                this.serverConnectionManager, this.consumerFilterManager, this.responseErrorHandler);
        if (methodDefinition.getRequestMode().equals(RequestMode.UNICAST)) {
            CompletableFuture<Object> resultFuture = new CompletableFuture<>();
            GenericFutureListener<io.netty.util.concurrent.Future<? super BulletResponse<Object>>> resultListener = f -> {
                try {
                    BulletResponse resp = (BulletResponse) f.get();
                    this.responseErrorHandler.errorHandle(resp);
                    resultFuture.complete(resp.getResponse());
                } catch (Exception e) {
                    resultFuture.completeExceptionally(e);
                }
            };
            List<GenericFutureListener<io.netty.util.concurrent.Future<? super BulletResponse<Object>>>> listeners = new ArrayList<>();
            listeners.add(resultListener);
            requestInvoker.asyncInvoke(methodDefinition, request, listeners);
            return resultFuture;
        }
        BroadcastRequestInvoker broadcastRequestInvoker = this.broadcastRequestInvokerFactory.getRequestInvoker(methodDefinition, request, requestInvoker);
        Map<String, CompletableFuture<Object>> responseMap = broadcastRequestInvoker.asyncInvoke(methodDefinition, request, (list) -> {
            CompletableFuture<Object> resultFuture = new CompletableFuture<>();
            GenericFutureListener<io.netty.util.concurrent.Future<? super BulletResponse<Object>>> resultListener = f -> {
                try {
                    BulletResponse resp = (BulletResponse) f.get();
                    this.responseErrorHandler.errorHandle(resp);
                    resultFuture.complete(resp.getResponse());
                } catch (Exception e) {
                    resultFuture.completeExceptionally(e);
                }
            };
            list.add(resultListener);
            return resultFuture;
        });
        return new BroadcastFuture(responseMap);
    }

    private BulletRequest<Object[]> createRequest(ConsumerMethodDefinition methodDefinition, Object... objects) {
        String action = ActionUrlUtils.getActionUrl(this.providerPath, this.methodPath);
        if (StrUtil.isEmpty(action)) {
            throw new ConsumerDefinitionException("Action url is empty.");
        }
        BulletRequest<Object[]> request;
        if (methodDefinition.isNotification()) {
            request = BulletRequest.createNotificationRequest(action);
        } else {
            request = BulletRequest.createActionRequest(action);
        }
        request.getHeader().setServerAddress(methodDefinition.getConsumerDefinition().getServerAddress());
        if (methodDefinition.getTransportArgumentsTypes() || objects != null) {
            List<String> typeNames = new ArrayList<>();
            for (int i = 0; i < objects.length; i++) {
                Object argument = objects[i];
                if (argument == null) {
                    typeNames.add(" ");
                    continue;
                }
                typeNames.add(argument.getClass().getTypeName());
            }
            request.getHeader().setConvertDesc(typeNames.stream().collect(Collectors.joining(",")));
        }
        request.setPayload(objects);
        return request;
    }


    private ConsumerMethodDefinition createVirtualMethodDefinition() {
        ConsumerMethodDefinition methodDefinition = new ConsumerMethodDefinition();
        methodDefinition.setReturnType(this.returnType);
        if (this.returnType == null) {
            throw new ConsumerDefinitionException("Return type must be set.");
        }
        if (this.returnType == null) {
            methodDefinition.setReturnType(Object.class);
        }
        methodDefinition.setArgumentsTypes(this.argumentsTypes);
        methodDefinition.setMethodName(this.methodPath);
        methodDefinition.setNotification(this.notification);
        methodDefinition.setRequestContentType(this.requestContentType);
        methodDefinition.setRequestTimeout(this.requestTimeout);
        methodDefinition.setRequestMode(this.requestMode);
        methodDefinition.setRetry(this.retry);
        methodDefinition.setRetryIntervalMs(this.retryIntervalMs);
        methodDefinition.setPreferResponseActualType(this.preferResponseActualType);
        methodDefinition.setTransportArgumentsTypes(this.transportArgumentsTypes);
        return methodDefinition;
    }

    private ConsumerMethodDefinition createVirtualMethodDefinition(Object... args) {
        ConsumerDefinition consumerDefinition = new ConsumerDefinition();
        if (StrUtil.isEmpty(this.serverAddress)) {
            throw new ConsumerDefinitionException("Server address must be set.");
        }
        consumerDefinition.setServerAddress(URI.create(this.serverAddress));
        consumerDefinition.setProviderPath(this.providerPath);
        ConsumerMethodDefinition methodDefinition = this.createVirtualMethodDefinition();
        methodDefinition.setConsumerDefinition(consumerDefinition);
        if (args == null) {
            methodDefinition.setArgumentsTypes(new Class[0]);
            return methodDefinition;
        }
        if (methodDefinition.getArgumentsTypes() == null) {
            Type[] types = new Type[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    types[i] = Object.class;
                    continue;
                }
                types[i] = args[i].getClass();
            }
        }
        return methodDefinition;
    }
}
