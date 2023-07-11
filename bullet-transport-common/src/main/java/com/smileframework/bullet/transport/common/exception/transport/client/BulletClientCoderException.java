package com.smileframework.bullet.transport.common.exception.transport.client;


public class BulletClientCoderException extends BulletClientTransportException {

    private String requestId;

    public BulletClientCoderException(String requestId) {
        this.requestId = requestId;
    }

    public BulletClientCoderException(String requestId, String message) {
        super(message);
        this.requestId = requestId;
    }

    public BulletClientCoderException(String requestId, String message, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
    }

    public BulletClientCoderException(String requestId, Throwable cause) {
        super(cause);
        this.requestId = requestId;
    }

    public BulletClientCoderException(String requestId, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
