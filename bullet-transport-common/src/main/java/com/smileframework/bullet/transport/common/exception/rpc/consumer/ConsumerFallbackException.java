package com.smileframework.bullet.transport.common.exception.rpc.consumer;


public class ConsumerFallbackException extends BulletConsumerException {

    public ConsumerFallbackException() {
    }

    public ConsumerFallbackException(String message) {
        super(message);
    }

    public ConsumerFallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsumerFallbackException(Throwable cause) {
        super(cause);
    }

    public ConsumerFallbackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
