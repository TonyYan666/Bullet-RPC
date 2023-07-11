package com.smileframework.bullet.transport.common.exception.rpc.serialization;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletContentConvertException extends BulletException {

    public BulletContentConvertException() {
    }

    public BulletContentConvertException(String message) {
        super(message);
    }

    public BulletContentConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletContentConvertException(Throwable cause) {
        super(cause);
    }

    public BulletContentConvertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
