package com.smileframework.bullet.transport.common.exception.rpc.consumer;

public class BulletRemoteInvokeException extends BulletConsumerException {

    public BulletRemoteInvokeException() {
    }

    public BulletRemoteInvokeException(String message) {
        super(message);
    }

    public BulletRemoteInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletRemoteInvokeException(Throwable cause) {
        super(cause);
    }

    public BulletRemoteInvokeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
