package com.smileframework.bullet.transport.common.exception.rpc.provider;

public class ProviderInvokeAuthenticationException extends BulletProviderException {

    public ProviderInvokeAuthenticationException() {
    }

    public ProviderInvokeAuthenticationException(String message) {
        super(message);
    }

    public ProviderInvokeAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderInvokeAuthenticationException(Throwable cause) {
        super(cause);
    }

    public ProviderInvokeAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
