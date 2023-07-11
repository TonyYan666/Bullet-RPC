package com.smileframework.bullet.rpc.provider;

import com.smileframework.bullet.rpc.provider.communication.handler.BulletActionServerHandler;
import com.smileframework.bullet.rpc.provider.communication.handler.BulletNotificationServerHandler;
import com.smileframework.bullet.rpc.provider.definition.ProviderDefinition;
import com.smileframework.bullet.rpc.provider.definition.ProviderDefinitionManager;
import com.smileframework.bullet.rpc.provider.invoker.BulletProviderInvoker;
import com.smileframework.bullet.rpc.provider.invoker.error.InvokeErrorTranslator;
import com.smileframework.bullet.rpc.provider.invoker.interceptor.ProviderInvokeInterceptor;
import com.smileframework.bullet.rpc.provider.invoker.interceptor.ProviderInvokeInterceptorManager;
import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderBusyException;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.server.BulletServerContext;
import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;

import java.util.concurrent.*;

public class BulletProviderContext extends BulletServerContext {

    @Getter
    private ProviderDefinitionManager providerDefinitionManager;

    @Getter
    private ProviderInvokeInterceptorManager providerInvokeInterceptorManager;

    @Getter
    private ExecutorService workExecutor;

    @Getter
    private InvokeErrorTranslator invokeErrorTranslator;

    @Getter
    private BulletActionServerHandler actionServerHandler;

    @Getter
    private BulletNotificationServerHandler notificationServerHandler;

    @Getter
    private BulletProviderInvoker bulletProviderInvoker;

    public BulletProviderContext() {
    }

    @Override
    public BulletServerContext init(BulletTransportServerConfig serverConfig) {
        super.init(serverConfig);
        return this;
    }

    @Override
    public BulletServerContext init(BulletTransportServerConfig serverConfig, ContentConvertManager contentConvertManager) {
        super.init(serverConfig, contentConvertManager);
        this.providerInit();
        return this;
    }

    private void providerInit() {
        this.setWorkExecutor(10, 50, 30, 6);
        this.providerDefinitionManager = new ProviderDefinitionManager();
        this.providerInvokeInterceptorManager = new ProviderInvokeInterceptorManager();
        this.invokeErrorTranslator = new InvokeErrorTranslator();
        this.bulletProviderInvoker = new BulletProviderInvoker(this.providerDefinitionManager, this.providerInvokeInterceptorManager, this.invokeErrorTranslator);
        this.notificationServerHandler = new BulletNotificationServerHandler(this.bulletProviderInvoker, this.providerDefinitionManager, this.workExecutor);
        this.actionServerHandler = new BulletActionServerHandler(this.bulletProviderInvoker, this.providerDefinitionManager, this.workExecutor);
        this.addServerCommunicatedHandler(this.actionServerHandler);
        this.addServerCommunicatedHandler(this.notificationServerHandler);
    }

    /**
     * 设置执行线程
     *
     * @param corePoolSize
     * @param maxPoolSize
     * @param keepAliveTimeSecond
     * @param blockingQueueSize
     * @return
     */
    public BulletServerContext setWorkExecutor(int corePoolSize, int maxPoolSize, int keepAliveTimeSecond, int blockingQueueSize) {
        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTimeSecond,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(blockingQueueSize, false),
                new DefaultThreadFactory("BulletProviderWorker"), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                throw new BulletProviderBusyException("Provider is busy.");
            }
        });
        ExecutorService oldWorkExecutor = this.workExecutor;
        this.workExecutor = executorService;
        if (this.notificationServerHandler != null) {
            this.notificationServerHandler.setExecutor(this.workExecutor);
        }
        if (this.actionServerHandler != null) {
            this.actionServerHandler.setExecutor(this.workExecutor);
        }
        if (oldWorkExecutor != null) {
            oldWorkExecutor.shutdown();
        }
        return this;
    }

    /**
     * 设置错误转换器
     *
     * @param invokeErrorTranslator
     * @return
     */
    public BulletServerContext setInvokeErrorTranslator(InvokeErrorTranslator invokeErrorTranslator) {
        this.invokeErrorTranslator = invokeErrorTranslator;
        if (this.bulletProviderInvoker != null) {
            this.bulletProviderInvoker.setInvokeErrorTranslator(invokeErrorTranslator);
        }
        return this;
    }

    /**
     * 添加执行拦截器
     *
     * @param invokeInterceptors
     */
    public BulletServerContext addInterceptor(ProviderInvokeInterceptor... invokeInterceptors) {
        for (ProviderInvokeInterceptor invokeInterceptor : invokeInterceptors) {
            this.providerInvokeInterceptorManager.addInterceptor(invokeInterceptor);
        }
        return this;
    }

    /**
     * 提供者注册
     *
     * @param provider
     * @param <T>
     */
    public <T> BulletServerContext providerRegister(T provider) {
        this.providerDefinitionManager.providerRegister(provider);
        return this;
    }

    /**
     * 提供者注册
     *
     * @param provider
     * @param <T>
     */
    public <T> BulletServerContext providerRegister(T provider, Class targetClz) {
        this.providerDefinitionManager.providerRegister(provider, targetClz);
        return this;
    }

    /**
     * 提供者注册
     *
     * @param providerDefinition
     * @param <T>
     */
    public <T> BulletServerContext providerRegister(ProviderDefinition<T> providerDefinition) {
        this.providerDefinitionManager.providerRegister(providerDefinition);
        return this;
    }


}
