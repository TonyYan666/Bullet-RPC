package com.smileframework.bullet.transport.common.exception.rpc.consumer;

import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;

public class ConsumerDefinitionException extends BulletConfigException {

    public ConsumerDefinitionException() {
    }

    public ConsumerDefinitionException(String message) {
        super(message);
    }

    public ConsumerDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsumerDefinitionException(Throwable cause) {
        super(cause);
    }

    public ConsumerDefinitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
