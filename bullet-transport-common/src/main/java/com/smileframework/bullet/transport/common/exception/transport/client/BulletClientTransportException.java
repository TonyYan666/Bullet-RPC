package com.smileframework.bullet.transport.common.exception.transport.client;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletClientTransportException extends BulletException {

    public BulletClientTransportException() {
    }

    public BulletClientTransportException(String message) {
        super(message);
    }

    public BulletClientTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletClientTransportException(Throwable cause) {
        super(cause);
    }

    public BulletClientTransportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
