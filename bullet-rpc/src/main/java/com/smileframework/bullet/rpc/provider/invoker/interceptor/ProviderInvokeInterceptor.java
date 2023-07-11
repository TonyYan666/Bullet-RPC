package com.smileframework.bullet.rpc.provider.invoker.interceptor;


import com.smileframework.bullet.rpc.provider.definition.ProviderMethodDefinition;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.server.connection.ClientConnection;

public interface ProviderInvokeInterceptor {

    boolean match(ProviderMethodDefinition methodDefinition,
                  BulletRequest<Object[]> request);

    /**
     * 执行调用之前
     *
     * @param methodDefinition 请求调用的方法定义
     * @param request          请求对象
     * @param clientConnection 客户端连接
     */
    void beforeMethodInvoke(ProviderMethodDefinition methodDefinition,
                            BulletRequest<Object[]> request,
                            ClientConnection clientConnection);

    /**
     * 执行成功调用之后
     *
     * @param methodDefinition 请求调用的方法定义
     * @param request          请求对象
     * @param response         响应对象
     * @param clientConnection 客户端连接
     */
    void afterMethodInvokeSuccess(ProviderMethodDefinition methodDefinition,
                                  BulletRequest<Object[]> request,
                                  BulletResponse<?> response,
                                  ClientConnection clientConnection,long costMs);

    /**
     * 执行提供者方法后，如果需要修改返回则直接返回你需要返回的对象，后续由序列化工具进行序列化操作
     *
     * @param methodDefinition 请求调用的方法定义
     * @param request          请求对象
     * @param response         响应对象
     * @param clientConnection 客户端连接
     * @param exception        异常
     * @return
     */
    void afterMethodInvokeFailure(ProviderMethodDefinition methodDefinition,
                                  BulletRequest<Object[]> request,
                                  BulletResponse<?> response,
                                  ClientConnection clientConnection,
                                  Throwable exception,long costMs);

    /**
     * 执行顺序 数字越小越优先
     *
     * @return
     */
    int order();


}
