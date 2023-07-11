package com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;

public interface RequestInvokeDecoratorBuilder {

    /**
     * 调用器优先级，数字越小越优先
     *
     * @return
     */
    int priority();

    /**
     * 是否匹配
     */
    boolean match(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);


    /**
     * 返回装饰者对象
     */
    ConsumerRequestInvoker build(ConsumerRequestInvoker requestInvoker, ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);

}
