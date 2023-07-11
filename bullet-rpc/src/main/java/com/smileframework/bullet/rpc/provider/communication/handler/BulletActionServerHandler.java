package com.smileframework.bullet.rpc.provider.communication.handler;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.provider.definition.ProviderDefinitionManager;
import com.smileframework.bullet.rpc.provider.definition.ProviderMethodDefinition;
import com.smileframework.bullet.rpc.provider.invoker.BulletProviderInvoker;
import com.smileframework.bullet.rpc.provider.invoker.context.ProviderInvokeContext;
import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderBusyException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import com.smileframework.bullet.transport.server.communication.handler.ServerCommunicatedHandler;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;

@Slf4j
public class BulletActionServerHandler implements ServerCommunicatedHandler<Object[]> {

    @Setter
    private Executor executor;

    private BulletProviderInvoker providerInvoker;

    private ProviderDefinitionManager providerManager;

    public BulletActionServerHandler(BulletProviderInvoker providerInvoker, ProviderDefinitionManager providerManager, Executor executor) {
        this.providerInvoker = providerInvoker;
        this.providerManager = providerManager;
        this.executor = executor;
    }

    @Override
    public int operationType() {
        return BulletRequestHeader.OPERATION_TYPE_ACTION;
    }

    @Override
    public void requestHandle(ClientConnection clientConnection, BulletRequest<Object[]> request) {
        final ProviderInvokeContext invokeContext = new ProviderInvokeContext();
        invokeContext.setRequest(request);
        invokeContext.setClientConnection(clientConnection);
        try {
            this.executor.execute(() -> {
                invokeContext.setWorkThread(Thread.currentThread());
                providerInvoker.invoke(invokeContext);
            });
        } catch (BulletProviderBusyException busyException) {
            log.error("[Bullet RPC] Provider is busy : " + request);
            BulletResponse response = BulletResponse.createErrorResponse(request,
                    BulletResponseCode.EXECUTOR_IS_BUSY, busyException, busyException.getMessage());
            clientConnection.sendResponse(response);
        } catch (Exception e) {
            log.error("[Bullet RPC] Provider executor submit error : " + request, e);
            BulletResponse response = BulletResponse.createErrorResponse(request, BulletResponseCode.INVOKE_ERROR, e, null);
            clientConnection.sendResponse(response);
        }
    }

    @Override
    public Type[] requestTypes(BulletRequest<Object[]> request) {
        ProviderMethodDefinition definition = this.providerManager.findProviderMethodDefinitionByCache(request.getHeader().getActionURL());
        if (!definition.getPreferArgumentsActualTypes() || StrUtil.isBlank(request.getHeader().getConvertDesc())) {
            return definition.getArgumentsTypes();
        }
        String[] typeNames = request.getHeader().getConvertDesc().split(",");
        if (typeNames.length != definition.getArgumentsTypes().length) {
            return definition.getArgumentsTypes();
        }
        Type[] actualTypes = new Type[typeNames.length];
        for (int i = 0; i < typeNames.length; i++) {
            String typeName = typeNames[i];
            Type type = definition.getArgumentsTypes()[i];
            if(StrUtil.isNotBlank(typeName)){
                try {
                    type = Class.forName(typeName);
                } catch (Exception e) {
                    //ignore exception keep using argument definition type;
                }
            }
            actualTypes[i] = type;
        }
        return actualTypes;
    }
}
