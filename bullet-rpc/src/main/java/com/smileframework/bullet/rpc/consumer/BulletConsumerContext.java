package com.smileframework.bullet.rpc.consumer;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerDefinition;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerDefinitionBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.ConsumerInvokeController;
import com.smileframework.bullet.rpc.consumer.invoke.fallback.factory.BaseFallbackHandlerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.fallback.factory.FallbackHandlerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerFilterManager;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreRequestFilter;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreResponseFilter;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvokerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.RequestInvokeDecoratorBuilder;
import com.smileframework.bullet.rpc.consumer.proxy.ServiceConsumerProxyFactory;
import com.smileframework.bullet.transport.client.BulletClientContext;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BulletConsumerContext extends BulletClientContext {

    private ConsumerFilterManager consumerFilterManager;

    private ConsumerRequestInvokerFactory consumerRequestInvokerFactory;


    private BroadcastRequestInvokerFactory broadcastRequestInvokerFactory;

    private Map<Class, Object> providerRefMap = new ConcurrentHashMap<>();

    private Map<Class, ConsumerInvokeController> consumerMap = new ConcurrentHashMap<>();

    private FallbackHandlerFactory fallbackHandlerFactory;

    public BulletConsumerContext(ContentConvertManager contentConvertManager) {
        super(contentConvertManager);
        this.init();
    }

    public BulletConsumerContext() {
        super();
        this.init();

    }

    private void init() {
        this.consumerFilterManager = new ConsumerFilterManager();
        this.consumerRequestInvokerFactory = new ConsumerRequestInvokerFactory();
        this.fallbackHandlerFactory = new BaseFallbackHandlerFactory();
        this.broadcastRequestInvokerFactory = new BroadcastRequestInvokerFactory();
    }

    /**
     * 添加请求过滤器
     *
     * @param requestFilter
     */
    public void addPreRequestFilter(ConsumerPreRequestFilter requestFilter) {
        this.consumerFilterManager.addPreRequestFilter(requestFilter);
    }

    /**
     * 添加响应过滤器
     *
     * @param responseFilter
     */
    public void addPreResponseFilter(ConsumerPreResponseFilter responseFilter) {
        this.consumerFilterManager.addPreResponseFilter(responseFilter);
    }

    /**
     * 添加请求执行器装饰者构建器
     */
    public void addRequestInvokeDecoratorBuilder(RequestInvokeDecoratorBuilder decoratorBuilder) {
        this.consumerRequestInvokerFactory.addInvokeDecoratorBuilder(decoratorBuilder);
    }

    /**
     * 添加广播调用器构建器
     *
     * @param broadcastRequestInvokerBuilder
     */
    public void addBroadcastRequestInvokerBuilder(BroadcastRequestInvokerBuilder broadcastRequestInvokerBuilder) {
        this.broadcastRequestInvokerFactory.addInvokerBuilder(broadcastRequestInvokerBuilder);
    }

    /**
     * 降级处理器工厂
     *
     * @param fallbackHandlerFactory
     */
    public void setFallbackHandlerFactory(FallbackHandlerFactory fallbackHandlerFactory) {
        this.fallbackHandlerFactory = fallbackHandlerFactory;
    }

    protected void setServerConnectionManager(ServerConnectionManager serverConnectionManager) {
        super.setServerConnectionManager(serverConnectionManager);
    }

    /**
     * 获得服务消费者代理
     *
     * @param consumerInterface
     * @param <T>
     * @return
     */
    public <T> T getServiceConsumerProxy(Class<T> consumerInterface) {
        this.isReadyCheck();
        if (this.providerRefMap.containsKey(consumerInterface)) {
            return (T) this.providerRefMap.get(consumerInterface);
        }
        ConsumerDefinition consumerDefinition = ConsumerDefinitionBuilder
                .create(consumerInterface, this.fallbackHandlerFactory)
                .createConsumerDefinition();
        ConsumerInvokeController invokeController = new ConsumerInvokeController(consumerDefinition)
                .setConsumerFilterManager(this.consumerFilterManager)
                .setRequestInvokerFactory(this.consumerRequestInvokerFactory)
                .setBroadcastRequestInvokerFactory(this.broadcastRequestInvokerFactory)
                .setResponseErrorHandler(this.getResponseErrorHandler())
                .setServerConnectionManager(this.getServerConnectionManager());
        T proxy = ServiceConsumerProxyFactory.createServiceConsumerRef(invokeController, consumerInterface);
        this.consumerMap.put(consumerInterface, invokeController);
        this.providerRefMap.put(consumerInterface, proxy);
        return proxy;
    }

    /**
     * 创建定义
     *
     * @param consumerInterface
     * @return
     */
    public ConsumerDefinition createConsumerDefinitionByInterface(Class consumerInterface) {
        ConsumerDefinition consumerDefinition = ConsumerDefinitionBuilder
                .create(consumerInterface, this.fallbackHandlerFactory)
                .createConsumerDefinition();
        return consumerDefinition;
    }

    /**
     * 创建并获取一个consumer 请求代理实例
     *
     * @param consumerDefinition
     * @param <T>
     * @return
     */
    public <T> T getServiceConsumerProxy(ConsumerDefinition consumerDefinition) {
        ConsumerInvokeController invokeController = new ConsumerInvokeController(consumerDefinition)
                .setConsumerFilterManager(this.consumerFilterManager)
                .setRequestInvokerFactory(this.consumerRequestInvokerFactory)
                .setResponseErrorHandler(this.getResponseErrorHandler())
                .setServerConnectionManager(this.getServerConnectionManager());
        T proxy = ServiceConsumerProxyFactory.createServiceConsumerRef(invokeController, consumerDefinition.getConsumerInterface());
        return proxy;
    }


}
