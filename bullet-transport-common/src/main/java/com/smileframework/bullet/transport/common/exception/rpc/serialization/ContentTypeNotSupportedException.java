package com.smileframework.bullet.transport.common.exception.rpc.serialization;

public class ContentTypeNotSupportedException extends BulletContentConvertException {

    public ContentTypeNotSupportedException() {
    }

    public ContentTypeNotSupportedException(String message) {
        super(message);
    }

    public ContentTypeNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentTypeNotSupportedException(Throwable cause) {
        super(cause);
    }

    public ContentTypeNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
