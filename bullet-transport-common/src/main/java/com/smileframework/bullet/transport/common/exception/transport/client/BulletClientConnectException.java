package com.smileframework.bullet.transport.common.exception.transport.client;

public class BulletClientConnectException extends BulletClientTransportException {

    public BulletClientConnectException() {
    }

    public BulletClientConnectException(String message) {
        super(message);
    }

    public BulletClientConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletClientConnectException(Throwable cause) {
        super(cause);
    }

    public BulletClientConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
