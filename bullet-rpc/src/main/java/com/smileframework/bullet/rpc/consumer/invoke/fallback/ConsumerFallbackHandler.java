package com.smileframework.bullet.rpc.consumer.invoke.fallback;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;

public interface ConsumerFallbackHandler {

    <T> T fallback(ConsumerMethodDefinition definition, Object[] arg, Exception e);

}
