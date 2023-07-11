package com.smileframework.bullet.transport.common.exception.transport.server;

import com.smileframework.bullet.transport.common.exception.BulletException;

public class BulletServerTransportException extends BulletException {

    public BulletServerTransportException() {
    }

    public BulletServerTransportException(String message) {
        super(message);
    }

    public BulletServerTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletServerTransportException(Throwable cause) {
        super(cause);
    }

    public BulletServerTransportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
