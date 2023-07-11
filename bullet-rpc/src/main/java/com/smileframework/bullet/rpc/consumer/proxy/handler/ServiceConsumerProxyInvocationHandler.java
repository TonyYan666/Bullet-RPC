package com.smileframework.bullet.rpc.consumer.proxy.handler;


import com.smileframework.bullet.rpc.consumer.invoke.ConsumerInvokeController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceConsumerProxyInvocationHandler implements InvocationHandler {

    private ConsumerInvokeController invokeController;

    public ServiceConsumerProxyInvocationHandler(ConsumerInvokeController invokeController) {
        this.invokeController = invokeController;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (invokeController.isAsyncFunction(method)) {
            return this.invokeController.asyncFunctionInvoke(method, objects);
        }
        if (invokeController.isMonoReactorFunction(method)) {
            return this.invokeController.reactiveMonoFunctionInvoke(method, objects);
        }
        return this.invokeController.functionInvoke(method, objects);
    }
}
