package com.smileframework.bullet.rpc.consumer.invoke;

import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response.BroadcastFuture;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerDefinitionException;
import com.smileframework.bullet.rpc.utils.ActionUrlUtils;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerDefinition;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerFilterManager;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvokerFactory;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response.BroadcastResultList;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.GenericFutureListener;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ConsumerInvokeController {

    private ConsumerDefinition consumerDefinition;

    private ServerConnectionManager serverConnectionManager;

    private ConsumerRequestInvokerFactory requestInvokerFactory;

    private BroadcastRequestInvokerFactory broadcastRequestInvokerFactory;

    private ResponseErrorHandler responseErrorHandler;

    private ConsumerFilterManager consumerFilterManager;

    public ConsumerInvokeController(ConsumerDefinition consumerDefinition) {
        this.consumerDefinition = consumerDefinition;
    }

    public ConsumerInvokeController setServerConnectionManager(ServerConnectionManager serverConnectionManager) {
        this.serverConnectionManager = serverConnectionManager;
        return this;
    }

    public ConsumerInvokeController setRequestInvokerFactory(ConsumerRequestInvokerFactory requestInvokerFactory) {
        this.requestInvokerFactory = requestInvokerFactory;
        return this;
    }

    public ConsumerInvokeController setResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;
        return this;
    }

    public ConsumerInvokeController setConsumerFilterManager(ConsumerFilterManager consumerFilterManager) {
        this.consumerFilterManager = consumerFilterManager;
        return this;
    }

    public ConsumerInvokeController setBroadcastRequestInvokerFactory(BroadcastRequestInvokerFactory broadcastRequestInvokerFactory) {
        this.broadcastRequestInvokerFactory = broadcastRequestInvokerFactory;
        return this;
    }

    public boolean isAsyncFunction(Method method) {
        ConsumerMethodDefinition methodDefinition = this.consumerDefinition.findMethodDefinition(method);
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        return methodDefinition.isAsync();
    }

    public boolean isMonoReactorFunction(Method method) {
        ConsumerMethodDefinition methodDefinition = this.consumerDefinition.findMethodDefinition(method);
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        return methodDefinition.isMonoReactor();
    }

    public Future<Object> asyncFunctionInvoke(Method method, Object[] objects) {
        ConsumerMethodDefinition methodDefinition = this.consumerDefinition.findMethodDefinition(method);
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        return this.asyncFunctionInvoke(methodDefinition, objects);
    }

    public Mono<Object> reactiveMonoFunctionInvoke(Method method, Object[] objects) {
        ConsumerMethodDefinition methodDefinition = this.consumerDefinition.findMethodDefinition(method);
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        return this.reactiveMonoFunctionInvoke(methodDefinition, objects);
    }

    public Object functionInvoke(Method method, Object[] objects) {
        ConsumerMethodDefinition methodDefinition = this.consumerDefinition.findMethodDefinition(method);
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        return functionInvoke(methodDefinition, objects);
    }

    public Object functionInvoke(String functionName, Object... objects) {
        ConsumerMethodDefinition methodDefinition = this.consumerDefinition.findMethodDefinitionByName(functionName);
        if (methodDefinition == null) {
            throw new ConsumerDefinitionException("Method not found from the consumer definition.");
        }
        return functionInvoke(methodDefinition, objects);
    }

    public Object functionInvoke(ConsumerMethodDefinition methodDefinition, Object... objects) {
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

    public Mono<Object> reactiveMonoFunctionInvoke(ConsumerMethodDefinition methodDefinition, Object... objects) {
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

    public Future<Object> asyncFunctionInvoke(ConsumerMethodDefinition methodDefinition, Object... objects) {
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
        String action = ActionUrlUtils.getActionUrl(this.consumerDefinition.getProviderPath(), methodDefinition.getMethodName());
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
}
