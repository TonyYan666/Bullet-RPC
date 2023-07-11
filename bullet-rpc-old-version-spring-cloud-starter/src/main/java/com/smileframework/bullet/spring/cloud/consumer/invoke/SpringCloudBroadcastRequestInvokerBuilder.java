package com.smileframework.bullet.spring.cloud.consumer.invoke;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerBuilder;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

public class SpringCloudBroadcastRequestInvokerBuilder implements BroadcastRequestInvokerBuilder {

    public final static int PRIORITY = 14;

    private DiscoveryClient discoveryClient;

    public SpringCloudBroadcastRequestInvokerBuilder(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean match(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        if (methodDefinition.getRequestMode().equals(RequestMode.BROADCAST)) {
            return true;
        }
        return false;
    }

    @Override
    public BroadcastRequestInvoker build(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return new SpringCloudBroadcastRequestInvoker(this.discoveryClient, requestInvoker);
    }

}
