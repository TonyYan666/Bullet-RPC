package com.smileframework.bullet.transport.common.exception.connection;

import com.smileframework.bullet.transport.common.exception.transport.server.BulletServerTransportException;

public class CheckServerConnectionException extends BulletServerTransportException {

    public CheckServerConnectionException() {
    }

    public CheckServerConnectionException(String message) {
        super(message);
    }

    public CheckServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckServerConnectionException(Throwable cause) {
        super(cause);
    }

    public CheckServerConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
