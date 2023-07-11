package com.smileframework.bullet.transport.common.exception.rpc.provider;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletProviderException extends BulletException {

    public BulletProviderException() {
    }

    public BulletProviderException(String message) {
        super(message);
    }

    public BulletProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletProviderException(Throwable cause) {
        super(cause);
    }

    public BulletProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
