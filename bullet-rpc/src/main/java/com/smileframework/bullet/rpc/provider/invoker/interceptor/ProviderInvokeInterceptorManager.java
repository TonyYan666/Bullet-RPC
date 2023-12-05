package com.smileframework.bullet.rpc.provider.invoker.interceptor;

import com.smileframework.bullet.rpc.provider.definition.ProviderMethodDefinition;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ProviderInvokeInterceptorManager {

    private List<ProviderInvokeInterceptor> invokeInterceptors = new CopyOnWriteArrayList<>();

    /**
     * 添加拦截器
     *
     * @param invokeInterceptors
     */
    public void addInterceptors(Collection<ProviderInvokeInterceptor> invokeInterceptors) {
        this.invokeInterceptors.addAll(invokeInterceptors);
        this.sortInterceptors();
    }

    /**
     * 添加调用拦截器
     *
     * @param invokeInterceptor
     */
    public void addInterceptor(ProviderInvokeInterceptor invokeInterceptor) {
        this.invokeInterceptors.add(invokeInterceptor);
        this.sortInterceptors();
    }

    /**
     * 拦截器排序
     */
    private void sortInterceptors() {
        this.invokeInterceptors.sort((a, b) -> Integer.valueOf(a.order()).compareTo(Integer.valueOf(b.order())));
    }


    /**
     * 执行调用之前
     *
     * @param methodDefinition 请求调用的方法定义
     * @param request          请求对象
     * @param clientConnection 客户端连接
     */
    public void beforeMethodInvoke(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request, ClientConnection clientConnection) {
        for (ProviderInvokeInterceptor invokeInterceptor : this.invokeInterceptors) {
            if(!invokeInterceptor.match(methodDefinition,request)){
                continue;
            }
            invokeInterceptor.beforeMethodInvoke(methodDefinition, request, clientConnection);
        }
    }

    /**
     * 执行成功调用之后
     *
     * @param methodDefinition 请求调用的方法定义
     * @param request          请求对象
     * @param response         响应对象
     * @param clientConnection 客户端连接
     */
    public void afterMethodInvokeSuccess(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<?> response, ClientConnection clientConnection, long costMs) {
        for (ProviderInvokeInterceptor invokeInterceptor : this.invokeInterceptors) {
            try {
                if(!invokeInterceptor.match(methodDefinition,request)){
                    continue;
                }
                invokeInterceptor.afterMethodInvokeSuccess(methodDefinition, request, response, clientConnection, costMs);
            } catch (Exception e) {
                log.error("[Bullet-Server] Provider invoke filter afterMethodInvokeSuccess method throw error.", e);
            }
        }
    }

    /**
     * 执行提供者方法后，如果需要修改返回则直接返回你需要返回的对象，后续由序列化工具进行序列化操作
     *
     * @param methodDefinition 请求调用的方法定义
     * @param request          请求对象
     * @param response         响应对象
     * @param clientConnection 客户端连接
     * @param exception        异常
     */
    public void afterMethodInvokeFailure(ProviderMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<?> response, ClientConnection clientConnection, Throwable exception, long costMs) {
        for (ProviderInvokeInterceptor invokeInterceptor : this.invokeInterceptors) {
            try {
                if(!invokeInterceptor.match(methodDefinition,request)){
                    continue;
                }
                invokeInterceptor.afterMethodInvokeFailure(methodDefinition, request, response, clientConnection, exception, costMs);
            }catch (Exception e){
                log.error("[Bullet-Server] Provider invoke filter afterMethodInvokeFailure method throw error.", e);
            }
        }
    }


}

