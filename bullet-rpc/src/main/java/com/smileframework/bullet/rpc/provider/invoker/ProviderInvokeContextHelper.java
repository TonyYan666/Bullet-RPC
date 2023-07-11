package com.smileframework.bullet.rpc.provider.invoker;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.smileframework.bullet.rpc.provider.invoker.context.ProviderInvokeContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供者调用上下文
 */
public class ProviderInvokeContextHelper {

    private static final TransmittableThreadLocal<String> currentRequestIdThreadLocal = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<ProviderInvokeContext> contextThreadLocal = new TransmittableThreadLocal<>();

    private static final Map<String, ProviderInvokeContext> currentRequestContexts = new ConcurrentHashMap<>();

    /**
     * 设置当前线程调用上下文
     *
     * @param context
     */
    protected static void setInvokeContext(ProviderInvokeContext context) {
        contextThreadLocal.set(context);
        currentRequestIdThreadLocal.set(context.getRequest().getHeader().getRequestId());
        currentRequestContexts.put(context.getRequest().getHeader().getRequestId(), context);
    }

    /**
     * 从当前线程获得调用上下文
     *
     * @return
     */
    public static ProviderInvokeContext getContext() {
        return contextThreadLocal.get();
    }

    /**
     * 释放上下文
     */
    public static void releaseContext() {
        currentRequestContexts.remove(currentRequestIdThreadLocal.get());
        contextThreadLocal.remove();
        currentRequestIdThreadLocal.remove();
    }


}