package com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;

public class FallbackRequestInvokerBuilder implements RequestInvokeDecoratorBuilder {

    public final static int PRIORITY = 50;

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean match(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        if (methodDefinition.getConsumerDefinition().getConsumerFallbackHandler() == null) {
            return false;
        }
        return true;
    }

    @Override
    public ConsumerRequestInvoker build(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return new FallbackRequestInvoker(requestInvoker);
    }
}
