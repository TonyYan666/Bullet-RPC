package com.smileframework.bullet.rpc.consumer.invoke.fallback.factory;

public interface FallbackHandlerFactory {

    <T> T getFallbackHandler(Class fallbackClz);

}
