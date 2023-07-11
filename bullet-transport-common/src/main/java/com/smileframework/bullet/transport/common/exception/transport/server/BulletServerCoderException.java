package com.smileframework.bullet.transport.common.exception.transport.server;

public class BulletServerCoderException extends BulletServerTransportException {

    public BulletServerCoderException() {
    }

    public BulletServerCoderException(String message) {
        super(message);
    }

    public BulletServerCoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BulletServerCoderException(Throwable cause) {
        super(cause);
    }

    public BulletServerCoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
