package com.smileframework.bullet.transport.common.exception.rpc.provider;

import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;

public class BulletProviderDefinitionException extends BulletConfigException {

    public BulletProviderDefinitionException() {
    }

    public BulletProviderDefinitionException(String message) {
        super(message);
    }

    public BulletProviderDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletProviderDefinitionException(Throwable cause) {
        super(cause);
    }

    public BulletProviderDefinitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
