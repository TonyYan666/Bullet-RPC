package com.smileframework.bullet.transport.common.exception.rpc.consumer;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletConsumerException extends BulletException {

    public BulletConsumerException() {
    }

    public BulletConsumerException(String message) {
        super(message);
    }

    public BulletConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletConsumerException(Throwable cause) {
        super(cause);
    }

    public BulletConsumerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
