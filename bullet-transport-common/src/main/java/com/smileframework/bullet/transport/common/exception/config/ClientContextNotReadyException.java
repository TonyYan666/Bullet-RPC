package com.smileframework.bullet.transport.common.exception.config;

public class ClientContextNotReadyException extends BulletConfigException {

    public ClientContextNotReadyException() {
    }

    public ClientContextNotReadyException(String message) {
        super(message);
    }

    public ClientContextNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientContextNotReadyException(Throwable cause) {
        super(cause);
    }

    public ClientContextNotReadyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}