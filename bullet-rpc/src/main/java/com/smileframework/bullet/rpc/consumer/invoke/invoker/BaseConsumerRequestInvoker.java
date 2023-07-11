package com.smileframework.bullet.rpc.consumer.invoke.invoker;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.future.PreferActualResponseTypesProvider;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerFilterManager;
import com.smileframework.bullet.transport.client.connection.ServerConnection;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.client.connection.future.DefaultResponseTypesProvider;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.BulletRemoteInvokeException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class BaseConsumerRequestInvoker implements ConsumerRequestInvoker {

    protected ServerConnectionManager connectionManager;

    protected ConsumerFilterManager filterManager;

    protected ResponseErrorHandler errorHandler;

    public BaseConsumerRequestInvoker(ServerConnectionManager connectionManager, ConsumerFilterManager filterManager, ResponseErrorHandler errorHandler) {
        this.connectionManager = connectionManager;
        this.filterManager = filterManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public BulletResponse<Object> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        ServerConnection connection = this.connectionManager.getServerConnection(request.getHeader().getServerAddress(), request);
        this.filterManager.doPreRequestFilter(methodDefinition, request, connection);
        BulletResponse<Object> response;
        Long startTime = System.currentTimeMillis();
        try {
            response = this.request(methodDefinition, request, connection);
            this.filterManager.doPreResponseFilter(methodDefinition, request, response, connection, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            this.filterManager.doPreResponseFilter(methodDefinition, request, e, connection, System.currentTimeMillis() - startTime);
            throw e;
        }
        return response;
    }

    @Override
    public BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners) {
        ServerConnection connection = this.connectionManager.getServerConnection(request.getHeader().getServerAddress(), request);
        this.filterManager.doPreRequestFilter(methodDefinition, request, connection);
        final Long startTime = System.currentTimeMillis();
        GenericFutureListener<Future<? super BulletResponse<Object>>> filterListener = internalFuture -> {
            try {
                BulletResponse<Object> finalResponse = (BulletResponse<Object>) internalFuture.get();
                this.filterManager.doPreResponseFilter(methodDefinition, request, finalResponse, connection, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                this.filterManager.doPreResponseFilter(methodDefinition, request, e, connection, System.currentTimeMillis() - startTime);
            }
        };
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(filterListener);
        BulletResponseFuture<?, Object> future = this.asyncRequest(methodDefinition, request, listeners, connection);
        return future;
    }

    @Override
    public Mono<BulletResponse<Object>> reactiveInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        ServerConnection connection = this.connectionManager.getServerConnection(request.getHeader().getServerAddress(), request);
        this.filterManager.doPreRequestFilter(methodDefinition, request, connection);
        final Long startTime = System.currentTimeMillis();
        return this.reactiveRequest(methodDefinition, request, connection)
                .doOnError(e ->
                        this.filterManager.doPreResponseFilter(methodDefinition, request, e, connection, System.currentTimeMillis() - startTime)
                ).doOnSuccess(response ->
                        this.filterManager.doPreResponseFilter(methodDefinition, request, response, connection, System.currentTimeMillis() - startTime)
                );
    }


    protected <T> BulletResponse<T> request(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, ServerConnection serverConnection) {
        try {
            BulletResponse<T> response = null;
            if (methodDefinition.getPreferResponseActualType()) {
                response = serverConnection.syncSendRequest(request, methodDefinition.getRequestTimeout(), new PreferActualResponseTypesProvider(new Type[]{methodDefinition.getReturnType()}));
            } else {
                response = serverConnection.syncSendRequest(request, methodDefinition.getRequestTimeout(), new Type[]{methodDefinition.getReturnType()});
            }
            return response;
        } catch (ExecutionException e) {
            throw new BulletRemoteInvokeException("Bullet rpc consumer invoke execution error.", e);
        } catch (InterruptedException e) {
            throw new BulletRemoteInvokeException("Bullet rpc consumer invoke execution interrupted.", e);
        } catch (TimeoutException e) {
            throw new BulletRemoteInvokeException("Bullet rpc consumer invoke more than " + methodDefinition.getRequestTimeout().toMillis() + " ms .", e);
        }
    }

    protected <T> BulletResponseFuture<?, T> asyncRequest(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request,
                                                          List<GenericFutureListener<Future<? super BulletResponse<T>>>> listeners,
                                                          ServerConnection serverConnection) {
        BulletResponseFuture<?, T> responseFuture = null;
        if (methodDefinition.getPreferResponseActualType()) {
            responseFuture = serverConnection.sendRequest(request, listeners, new PreferActualResponseTypesProvider(new Type[]{methodDefinition.getReturnType()}));
        } else {
            responseFuture = serverConnection.sendRequest(request, listeners, new Type[]{methodDefinition.getReturnType()});
        }
        return responseFuture;
    }

    protected <T> Mono<BulletResponse<T>> reactiveRequest(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request,
                                                          ServerConnection serverConnection) {
        if (methodDefinition.getPreferResponseActualType()) {
            return serverConnection.sendReactiveRequest(request, new PreferActualResponseTypesProvider(new Type[]{methodDefinition.getReturnType()}));
        }
        return serverConnection.sendReactiveRequest(request, new DefaultResponseTypesProvider(new Type[]{methodDefinition.getReturnType()}));
    }

}
