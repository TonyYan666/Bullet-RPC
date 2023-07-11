package com.smileframework.bullet.transport.common.exception.authentication;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletChannelAuthenticationError extends BulletException {

    public BulletChannelAuthenticationError() {
    }

    public BulletChannelAuthenticationError(String message) {
        super(message);
    }

    public BulletChannelAuthenticationError(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletChannelAuthenticationError(Throwable cause) {
        super(cause);
    }

    public BulletChannelAuthenticationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
