package com.smileframework.bullet.rpc.consumer.invoke.invoker;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerFilterManager;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.FallbackRequestInvokerBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.RequestInvokeDecoratorBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.RetryRequestInvokerBuilder;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;

import java.util.ArrayList;
import java.util.List;

public class ConsumerRequestInvokerFactory {

    private List<RequestInvokeDecoratorBuilder> requestInvokeDecoratorBuilderList = new ArrayList<>();

    public ConsumerRequestInvokerFactory() {
        init();
    }

    public void init() {
        this.requestInvokeDecoratorBuilderList.add(new RetryRequestInvokerBuilder());
        this.requestInvokeDecoratorBuilderList.add(new FallbackRequestInvokerBuilder());
        this.requestInvokeDecoratorBuilderList.sort((a, b) -> Integer.valueOf(a.priority()).compareTo(Integer.valueOf(b.priority())));
    }

    /**
     * 添加执行器
     */
    public void addInvokeDecoratorBuilder(RequestInvokeDecoratorBuilder decoratorBuilder) {
        this.requestInvokeDecoratorBuilderList.add(decoratorBuilder);
        this.requestInvokeDecoratorBuilderList.sort((a, b) -> Integer.valueOf(a.priority()).compareTo(Integer.valueOf(b.priority())));
    }

    /**
     * 按照请求与定义获得响应的执行器
     * 不同的执行器有不同的功能，例如支持重试的执行器、支持降级的执行器等等
     */
    public ConsumerRequestInvoker getRequestInvoker(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, ServerConnectionManager connectionManager, ConsumerFilterManager filterManager, ResponseErrorHandler errorHandler) {
        ConsumerRequestInvoker requestInvoker = new BaseConsumerRequestInvoker(connectionManager, filterManager, errorHandler);
        for (RequestInvokeDecoratorBuilder decoratorBuilder : this.requestInvokeDecoratorBuilderList) {
            if (decoratorBuilder.match(requestInvoker, methodDefinition, request)) {
                requestInvoker = decoratorBuilder.build(requestInvoker, methodDefinition, request);
            }
        }
        return requestInvoker;
    }

}
