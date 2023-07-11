package com.smileframework.bullet.transport.client.error.impl;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.transport.server.OperationTypeUnsupportedException;
import com.smileframework.bullet.transport.common.exception.authentication.BulletChannelAuthenticationError;
import com.smileframework.bullet.transport.common.exception.rpc.consumer.BulletRemoteInvokeException;
import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderBusyException;
import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderDefinitionException;
import com.smileframework.bullet.transport.common.exception.rpc.provider.ProviderInvokeAuthenticationException;
import com.smileframework.bullet.transport.common.exception.rpc.serialization.BulletContentConvertException;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;

import java.lang.reflect.Constructor;

public class BaseResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public BulletResponse errorHandle(BulletResponse response) {
        String code = response.getHeader().getCode();
        if (BulletResponseCode.SUCCESS.equals(code)) {
            return response;
        }
        if (BulletResponseCode.TRANSPORT_AUTHENTICATION_FAIL.equals(code)) {
            throw new BulletChannelAuthenticationError(response.getHeader().getErrorReason());
        }
        if (BulletResponseCode.PROVIDER_NOT_FOUND.equals(code)) {
            throw new BulletProviderDefinitionException(response.getHeader().getErrorReason());
        }
        if (BulletResponseCode.EXECUTION_AUTHENTICATION_FAIL.equals(code)) {
            throw new ProviderInvokeAuthenticationException(response.getHeader().getErrorReason());
        }
        if (BulletResponseCode.OPERATION_TYPE_UNSUPPORTED.equals(code)) {
            throw new OperationTypeUnsupportedException(response.getHeader().getErrorReason());
        }
        if (BulletResponseCode.EXECUTOR_IS_BUSY.equals(code)) {
            throw new BulletProviderBusyException(response.getHeader().getErrorReason());
        }
        if (BulletResponseCode.CONTENT_TYPE_CONVERT_FAILURE.equals(code)) {
            throw new BulletContentConvertException(response.getHeader().getErrorReason());
        }
        if (BulletResponseCode.INVOKE_ERROR.equals(code)) {
            if (StrUtil.isNotBlank(response.getHeader().getExceptionClz())) {
                Throwable throwable = null;
                try {
                    Class<?> throwableClz = ClassLoaderUtil.getClassLoader().loadClass(response.getHeader().getExceptionClz());
                    Constructor constructor = throwableClz.getConstructor(String.class);
                    throwable = (Throwable) constructor.newInstance(response.getHeader().getErrorReason());
                } catch (Throwable e) {
                    //ignore
                }
                if (throwable != null) {
                    if (RuntimeException.class.isAssignableFrom(throwable.getClass())) {
                        throw (RuntimeException) throwable;
                    } else {
                        throw new BulletRemoteInvokeException("remote invoke error", throwable);
                    }
                }
                throw new BulletRemoteInvokeException("bullet remote invoke error. [" + code + "] " + response.getHeader().getErrorReason());
            }
        }
        return response;
    }
}
