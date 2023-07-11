package com.smileframework.bullet.transport.common.exception.transport.server;

public class ClientConnectionManagerException extends BulletServerTransportException {

    public ClientConnectionManagerException() {
    }

    public ClientConnectionManagerException(String message) {
        super(message);
    }

    public ClientConnectionManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientConnectionManagerException(Throwable cause) {
        super(cause);
    }

    public ClientConnectionManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
