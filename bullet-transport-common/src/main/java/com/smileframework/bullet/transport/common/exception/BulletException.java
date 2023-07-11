package com.smileframework.bullet.transport.common.exception;


public class BulletException extends RuntimeException {

    public BulletException() {
    }

    public BulletException(String message) {
        super(message);
    }

    public BulletException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletException(Throwable cause) {
        super(cause);
    }

    public BulletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
