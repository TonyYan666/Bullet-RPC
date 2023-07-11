package com.smileframework.bullet.rpc.spring.common.config;

import com.smileframework.bullet.rpc.consumer.invoke.fallback.factory.FallbackHandlerFactory;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreRequestFilter;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreResponseFilter;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.RequestInvokeDecoratorBuilder;
import com.smileframework.bullet.rpc.provider.invoker.error.InvokeErrorTranslator;
import com.smileframework.bullet.rpc.provider.invoker.interceptor.ProviderInvokeInterceptor;
import com.smileframework.bullet.transport.client.connection.handshake.HandshakeInfoProvider;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.config.BulletConfigException;
import com.smileframework.bullet.transport.common.protocol.serialization.handler.ContentConvertor;
import com.smileframework.bullet.transport.server.authentication.ConnectionAuthenticationManager;
import com.smileframework.bullet.transport.server.connection.handshake.HandshakeEventListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Bullet RPC Spring 配置器
 */
public class BulletRpcConfig {

    private BulletRpcProviderConfig providerConfig = new BulletRpcProviderConfig(this);

    private BulletRpcConsumerConfig consumerConfig = new BulletRpcConsumerConfig(this);

    @Getter
    private List<ContentConvertor> contentConvertors = new ArrayList<>();

    protected boolean configurable = true;

    public BulletRpcProviderConfig provider() {
        return this.providerConfig;
    }

    public BulletRpcConsumerConfig consumer() {
        return this.consumerConfig;
    }

    public boolean configurable() {
        return this.configurable;
    }

    protected void configured() {
        this.configurable = false;
    }

    protected void checkConfigStatus() {
        if (!this.configurable) {
            throw new BulletConfigException("Bullet rpc has already configured.");
        }
    }

    public BulletRpcConfig replaceContentConvertor(ContentConvertor convertor) {
        this.checkConfigStatus();
        this.contentConvertors.add(convertor);
        return this;
    }

    public class BulletRpcProviderConfig {

        @Getter
        private List<ProviderInvokeInterceptor> providerInvokeInterceptors = new ArrayList<>();

        @Getter
        private InvokeErrorTranslator invokeErrorTranslator = null;

        @Getter
        private List<HandshakeEventListener> handshakeEventListeners = new ArrayList<>();

        @Getter
        private ConnectionAuthenticationManager connectionAuthenticationManager;

        private BulletRpcConfig rpcConfig;

        public BulletRpcProviderConfig(BulletRpcConfig rpcConfig) {
            this.rpcConfig = rpcConfig;
        }

        public BulletRpcProviderConfig setInvokeErrorTranslator(InvokeErrorTranslator invokeErrorTranslator) {
            this.rpcConfig.checkConfigStatus();
            this.invokeErrorTranslator = invokeErrorTranslator;
            return this;
        }

        public BulletRpcProviderConfig addHandshakeEventListener(HandshakeEventListener... listeners) {
            this.rpcConfig.checkConfigStatus();
            if (listeners == null) {
                return this;
            }
            for (HandshakeEventListener listener : listeners) {
                this.handshakeEventListeners.add(listener);
            }
            return this;
        }

        public BulletRpcProviderConfig setConnectionAuthenticationManager(ConnectionAuthenticationManager connectionAuthenticationManager) {
            this.rpcConfig.checkConfigStatus();
            this.connectionAuthenticationManager = connectionAuthenticationManager;
            return this;
        }

        public BulletRpcProviderConfig addInvokeInterceptor(ProviderInvokeInterceptor... invokeInterceptors) {
            this.rpcConfig.checkConfigStatus();
            if (invokeInterceptors == null) {
                return this;
            }
            for (ProviderInvokeInterceptor invokeInterceptor : invokeInterceptors) {
                this.providerInvokeInterceptors.add(invokeInterceptor);
            }
            return this;
        }

        public BulletRpcConfig and() {
            return this.rpcConfig;
        }
    }

    public class BulletRpcConsumerConfig {

        private BulletRpcConfig rpcConfig;

        @Getter
        private Set<ConsumerPreRequestFilter> consumerPreRequestFilters = new HashSet<>();

        @Getter
        private Set<ConsumerPreResponseFilter> consumerPreResponseFilters = new HashSet<>();

        @Getter
        private Set<RequestInvokeDecoratorBuilder> requestInvokeDecoratorBuilders = new HashSet<>();

        @Getter
        private Set<BroadcastRequestInvokerBuilder> broadcastRequestInvokerBuilders = new HashSet<>();

        @Getter
        private HandshakeInfoProvider handshakeInfoProvider;

        @Getter
        private ResponseErrorHandler responseErrorHandler;

        @Getter
        private FallbackHandlerFactory fallbackHandlerFactory;

        public BulletRpcConsumerConfig(BulletRpcConfig rpcConfig) {
            this.rpcConfig = rpcConfig;
        }

        /**
         * 设置响应错误处理器
         *
         * @param responseErrorHandler
         * @return
         */
        public BulletRpcConsumerConfig setResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            this.rpcConfig.checkConfigStatus();
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        /**
         * 添加请求过滤器
         *
         * @param requestFilter
         */
        public BulletRpcConsumerConfig addPreRequestFilter(ConsumerPreRequestFilter requestFilter) {
            this.rpcConfig.checkConfigStatus();
            this.consumerPreRequestFilters.add(requestFilter);
            return this;
        }

        /**
         * 添加响应过滤器
         *
         * @param responseFilter
         */
        public BulletRpcConsumerConfig addPreResponseFilter(ConsumerPreResponseFilter responseFilter) {
            this.rpcConfig.checkConfigStatus();
            this.consumerPreResponseFilters.add(responseFilter);
            return this;
        }

        /**
         * 添加握手信息提供者
         *
         * @param handshakeInfoProvider
         * @return
         */
        public BulletRpcConsumerConfig setHandshakeInfoProvider(HandshakeInfoProvider handshakeInfoProvider) {
            this.rpcConfig.checkConfigStatus();
            this.handshakeInfoProvider = handshakeInfoProvider;
            return this;
        }

        /**
         * 添加调用执行器装饰者构造器
         *
         * @return
         */
        public BulletRpcConsumerConfig addRequestInvokeDecoratorBuilder(RequestInvokeDecoratorBuilder decoratorBuilder) {
            this.rpcConfig.checkConfigStatus();
            this.requestInvokeDecoratorBuilders.add(decoratorBuilder);
            return this;
        }

        /**
         * 添加广播调用构造器
         *
         * @param broadcastRequestInvokerBuilder
         * @return
         */
        public BulletRpcConsumerConfig addBroadcastRequestInvokerBuilder(BroadcastRequestInvokerBuilder broadcastRequestInvokerBuilder) {
            this.rpcConfig.checkConfigStatus();
            this.broadcastRequestInvokerBuilders.add(broadcastRequestInvokerBuilder);
            return this;
        }

        /**
         * 降级处理器工厂
         *
         * @param fallbackHandlerFactory
         */
        public BulletRpcConsumerConfig setFallbackHandlerFactory(FallbackHandlerFactory fallbackHandlerFactory) {
            this.rpcConfig.checkConfigStatus();
            this.fallbackHandlerFactory = fallbackHandlerFactory;
            return this;
        }

        public BulletRpcConfig and() {
            return this.rpcConfig;
        }
    }

}
