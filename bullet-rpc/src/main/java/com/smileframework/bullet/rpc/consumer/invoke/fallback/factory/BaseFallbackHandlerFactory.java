package com.smileframework.bullet.rpc.consumer.invoke.fallback.factory;

import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerFallbackException;

public class BaseFallbackHandlerFactory implements FallbackHandlerFactory {

    @Override
    public <T> T getFallbackHandler(Class fallbackClz) {
        try {
            return (T) fallbackClz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ConsumerFallbackException("Get fallback handler error.", e);
        }
    }
}
