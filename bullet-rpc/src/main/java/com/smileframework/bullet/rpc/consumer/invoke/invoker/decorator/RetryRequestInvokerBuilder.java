package com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;

public class RetryRequestInvokerBuilder implements RequestInvokeDecoratorBuilder {

    public final static int PRIORITY = 30;

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean match(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return methodDefinition.getRetry() > 0;
    }

    @Override
    public ConsumerRequestInvoker build(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return new RetryRequestInvoker(requestInvoker);
    }
}
