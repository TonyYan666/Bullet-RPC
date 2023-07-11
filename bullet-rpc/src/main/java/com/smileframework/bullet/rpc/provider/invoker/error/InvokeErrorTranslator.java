package com.smileframework.bullet.rpc.provider.invoker.error;

import com.smileframework.bullet.transport.common.exception.rpc.provider.ProviderInvokeAuthenticationException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;

import java.nio.file.ProviderNotFoundException;

public class InvokeErrorTranslator {

    public BulletResponse<?> exceptionTranslateToResponse(BulletRequest<Object[]> request, Throwable exception) {
        if (request.getHeader().getOperationType() != BulletRequestHeader.OPERATION_TYPE_ACTION) {
            return null;
        }
        BulletResponse response = null;
        if (exception.getClass().isAssignableFrom(ProviderNotFoundException.class)) {
            response = BulletResponse.createErrorResponse(request, BulletResponseCode.PROVIDER_NOT_FOUND, exception, exception.getMessage());
        } else if (exception.getClass().isAssignableFrom(ProviderInvokeAuthenticationException.class)) {
            response = BulletResponse.createErrorResponse(request, BulletResponseCode.EXECUTION_AUTHENTICATION_FAIL, exception, exception.getMessage());
        } else {
            response = BulletResponse.createErrorResponse(request, BulletResponseCode.INVOKE_ERROR, exception, null);
        }
        return response;
    }

}
