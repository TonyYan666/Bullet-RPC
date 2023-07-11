package com.smileframework.bullet.transport.common.exception.rpc.provider;

public class BulletProviderBusyException extends BulletProviderException {

    public BulletProviderBusyException() {
    }

    public BulletProviderBusyException(String message) {
        super(message);
    }

    public BulletProviderBusyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletProviderBusyException(Throwable cause) {
        super(cause);
    }

    public BulletProviderBusyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
