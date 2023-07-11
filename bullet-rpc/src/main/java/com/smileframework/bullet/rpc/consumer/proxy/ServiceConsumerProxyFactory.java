package com.smileframework.bullet.rpc.consumer.proxy;


import com.smileframework.bullet.rpc.consumer.invoke.ConsumerInvokeController;
import com.smileframework.bullet.rpc.consumer.proxy.handler.ServiceConsumerProxyInvocationHandler;

import java.lang.reflect.Proxy;

public class ServiceConsumerProxyFactory {

    public static <T> T createServiceConsumerRef(ConsumerInvokeController invokeController, Class<?> providerInterface) {
        ClassLoader classLoader = ServiceConsumerProxyFactory.class.getClassLoader();
        ServiceConsumerProxyInvocationHandler invocationHandler = new ServiceConsumerProxyInvocationHandler(invokeController);
        Class<?>[] interfaces = {providerInterface};
        return (T) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

}
