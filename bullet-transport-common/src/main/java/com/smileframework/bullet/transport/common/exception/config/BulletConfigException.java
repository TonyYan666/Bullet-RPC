package com.smileframework.bullet.transport.common.exception.config;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletConfigException extends BulletException {

    public BulletConfigException() {
    }

    public BulletConfigException(String message) {
        super(message);
    }

    public BulletConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletConfigException(Throwable cause) {
        super(cause);
    }

    public BulletConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
