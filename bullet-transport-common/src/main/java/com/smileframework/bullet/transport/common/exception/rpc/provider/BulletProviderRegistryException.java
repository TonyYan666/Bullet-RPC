package com.smileframework.bullet.transport.common.exception.rpc.provider;


import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;

public class BulletProviderRegistryException extends BulletConfigException {

    public BulletProviderRegistryException() {
    }

    public BulletProviderRegistryException(String message) {
        super(message);
    }

    public BulletProviderRegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletProviderRegistryException(Throwable cause) {
        super(cause);
    }

    public BulletProviderRegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
