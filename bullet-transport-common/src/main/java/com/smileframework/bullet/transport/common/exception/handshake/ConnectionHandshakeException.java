package com.smileframework.bullet.transport.common.exception.handshake;

import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientTransportException;

public class ConnectionHandshakeException extends BulletClientTransportException {

    public ConnectionHandshakeException() {
    }

    public ConnectionHandshakeException(String message) {
        super(message);
    }

    public ConnectionHandshakeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionHandshakeException(Throwable cause) {
        super(cause);
    }

    public ConnectionHandshakeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
