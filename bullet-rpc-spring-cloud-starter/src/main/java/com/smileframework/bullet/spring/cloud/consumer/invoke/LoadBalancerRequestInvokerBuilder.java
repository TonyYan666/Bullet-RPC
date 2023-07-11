package com.smileframework.bullet.spring.cloud.consumer.invoke;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.RequestInvokeDecoratorBuilder;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

public class LoadBalancerRequestInvokerBuilder implements RequestInvokeDecoratorBuilder {

    public final static int PRIORITY = 15;

    private final LoadBalancerClientFactory loadBalancerClientFactory;

    private final LoadBalancerClient loadBalancerClient;

    public LoadBalancerRequestInvokerBuilder(LoadBalancerClientFactory loadBalancerClientFactory, LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClientFactory = loadBalancerClientFactory;
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean match(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        if (methodDefinition.getRequestMode().equals(RequestMode.UNICAST)) {
            return true;
        }
        return false;
    }

    @Override
    public ConsumerRequestInvoker build(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return new LoadBalancerRequestInvoker(loadBalancerClientFactory, loadBalancerClient, requestInvoker);
    }
}
