package com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast;

import cn.hutool.core.collection.CollectionUtil;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;

import java.util.ArrayList;
import java.util.List;

public class BroadcastRequestInvokerFactory {

    private List<BroadcastRequestInvokerBuilder> broadcastRequestInvokerBuilders = new ArrayList<>();

    public BroadcastRequestInvokerFactory() {
        init();
    }

    public void init() {
        //暂时没有默认提供的广播调用器
    }

    /**
     * 添加执行器
     */
    public void addInvokerBuilder(BroadcastRequestInvokerBuilder invokerBuilder) {
        this.broadcastRequestInvokerBuilders.add(invokerBuilder);
        this.broadcastRequestInvokerBuilders.sort((a, b) -> Integer.valueOf(a.priority()).compareTo(Integer.valueOf(b.priority())));
    }

    /**
     * 按照请求与定义获得响应的执行器
     * 不同的执行器有不同的功能，例如支持重试的执行器、支持降级的执行器等等
     */
    public BroadcastRequestInvoker getRequestInvoker(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, ConsumerRequestInvoker consumerRequestInvoker) {
        if (CollectionUtil.isEmpty(this.broadcastRequestInvokerBuilders)) {
            throw new UnsupportedOperationException("Haven't found any broadcast request invoker builder. unsupported broadcast invoker if broadcast request invoker not found. ");
        }
        for (BroadcastRequestInvokerBuilder broadcastRequestInvokerBuilder : this.broadcastRequestInvokerBuilders) {
            if (!broadcastRequestInvokerBuilder.match(consumerRequestInvoker, methodDefinition, request)) {
                continue;
            }
            return broadcastRequestInvokerBuilder.build(consumerRequestInvoker, methodDefinition, request);
        }
        throw new UnsupportedOperationException("Matched no one broadcast request invoker builder. unsupported broadcast invoker if all broadcast request invoker not matched. ");
    }

}
