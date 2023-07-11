package com.smileframework.bullet.rpc.provider.invoker.log;


import com.smileframework.bullet.rpc.provider.definition.ProviderMethodDefinition;
import com.smileframework.bullet.rpc.provider.invoker.interceptor.ProviderInvokeInterceptor;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class ProviderInvokeLoggerInterceptor implements ProviderInvokeInterceptor {

    @Override
    public boolean match(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        return true;
    }

    @Override
    public void beforeMethodInvoke(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request, ClientConnection clientConnection) {

    }

    @Override
    public void afterMethodInvokeSuccess(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<?> response, ClientConnection clientConnection, long costMs) {
        String location = methodDefinition.getProviderDefinition().getProviderClz().getName() + "#" + methodDefinition.getMethodName();
        log.info("[Bullet-Provider] request method ->  " + location + " success cost[" + costMs + "ms]. args[" + Arrays.toString(request.getPayload()) + "] response[" + response.getResponse() + "]");
    }

    @Override
    public void afterMethodInvokeFailure(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<?> response, ClientConnection clientConnection, Throwable exception, long costMs) {
        String location = methodDefinition.getProviderDefinition().getProviderClz().getName() + "#" + methodDefinition.getMethodName();
        log.error("[Bullet-Provider] request method ->  " + location + " error cost[" + costMs + "ms]. args[" + Arrays.toString(request.getPayload()) + "] response-header[" + response.getHeader() + "].", exception);
    }

    @Override
    public int order() {
        return 0;
    }
}
