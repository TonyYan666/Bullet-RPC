package com.smileframework.bullet.rpc.spring.common.exception;

import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;

public class ProviderCreatedException extends BulletConfigException {

    public ProviderCreatedException() {
    }

    public ProviderCreatedException(String message) {
        super(message);
    }

    public ProviderCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderCreatedException(Throwable cause) {
        super(cause);
    }

    public ProviderCreatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
