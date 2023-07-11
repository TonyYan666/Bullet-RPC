package com.smileframework.bullet.rpc.provider.invoker;


import com.smileframework.bullet.rpc.provider.definition.ProviderDefinitionManager;
import com.smileframework.bullet.rpc.provider.definition.ProviderMethodDefinition;
import com.smileframework.bullet.rpc.provider.invoker.interceptor.ProviderInvokeInterceptorManager;
import com.smileframework.bullet.rpc.provider.invoker.context.ProviderInvokeContext;
import com.smileframework.bullet.rpc.provider.invoker.error.InvokeErrorTranslator;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BulletProviderInvoker {

    private ProviderDefinitionManager providerManager;

    private ProviderInvokeInterceptorManager invokeInterceptorManager;

    @Setter
    private InvokeErrorTranslator invokeErrorTranslator;

    public BulletProviderInvoker(ProviderDefinitionManager providerManager, ProviderInvokeInterceptorManager invokeInterceptorManager, InvokeErrorTranslator invokeErrorTranslator) {
        this.providerManager = providerManager;
        this.invokeInterceptorManager = invokeInterceptorManager;
        this.invokeErrorTranslator = invokeErrorTranslator;
    }

    public void invoke(ProviderInvokeContext invokeContext) {
        BulletRequest<Object[]> request = invokeContext.getRequest();
        ClientConnection connection = invokeContext.getClientConnection();
        ProviderInvokeContextHelper.setInvokeContext(invokeContext);
        ProviderMethodDefinition methodDefinition = null;
        Long startTime = System.currentTimeMillis();
        try {
            methodDefinition = this.findMethodDefinition(invokeContext);
            this.invokeInterceptorManager.beforeMethodInvoke(methodDefinition, request, connection);
            startTime = System.currentTimeMillis();
            Object result = this.functionInvoke(invokeContext.getRequest().getPayload(), methodDefinition);
            BulletResponse<?> response = BulletResponse.createSuccessResponse(request, result);
            //是否需要携带返回的实际类型，如果需要则将类型全额定类名通过header的convertDesc 转换描述中携带
            if (invokeContext.getMethodDefinition().getTransportResponseType()) {
                if (result != null) {
                    response.getHeader().setConvertDesc(result.getClass().getTypeName());
                }
            }
            this.invokeInterceptorManager.afterMethodInvokeSuccess(methodDefinition, request, response, connection, System.currentTimeMillis() - startTime);
            if (invokeContext.getRequest().getHeader().getOperationType() == BulletRequestHeader.OPERATION_TYPE_ACTION) {
                connection.sendResponse(response);
            }
        } catch (Throwable exception) {
            BulletResponse<?> response = this.invokeErrorTranslator.exceptionTranslateToResponse(request, exception);
            this.invokeInterceptorManager.afterMethodInvokeFailure(methodDefinition, request, response, connection, exception, System.currentTimeMillis() - startTime);
            connection.sendResponse(response);
        } finally {
            ProviderInvokeContextHelper.releaseContext();
        }
    }

    private Object functionInvoke(Object[] arguments, ProviderMethodDefinition methodDefinition) throws Throwable {
        Object provider = methodDefinition.getProviderDefinition().getProvider();
        Method method = methodDefinition.getProviderMethod();
        Object returnVal = null;
        try {
            returnVal = method.invoke(provider, arguments);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            } else {
                throw e;
            }
        }
        return returnVal;
    }

    private ProviderMethodDefinition findMethodDefinition(ProviderInvokeContext context) {
        ProviderMethodDefinition definition = this.providerManager.findProviderMethodDefinitionByCache(context.getRequest().getHeader().getActionURL());
        context.setMethodDefinition(definition);
        return definition;
    }

}
