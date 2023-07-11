package com.smileframework.bullet.transport.common.exception.transport.server;

public class OperationTypeUnsupportedException extends BulletServerTransportException {

    public OperationTypeUnsupportedException() {
    }

    public OperationTypeUnsupportedException(String message) {
        super(message);
    }

    public OperationTypeUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationTypeUnsupportedException(Throwable cause) {
        super(cause);
    }

    public OperationTypeUnsupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
