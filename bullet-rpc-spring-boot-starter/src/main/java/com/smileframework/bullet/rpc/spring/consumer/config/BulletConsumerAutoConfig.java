package com.smileframework.bullet.rpc.spring.consumer.config;

import com.smileframework.bullet.rpc.spring.common.config.BulletCommonAutoConfig;
import com.smileframework.bullet.rpc.spring.common.config.BulletRpcConfig;
import com.smileframework.bullet.rpc.spring.consumer.SpringBulletConsumerContext;
import com.smileframework.bullet.rpc.spring.consumer.bean.registrar.BulletConsumerRegistrar;
import com.smileframework.bullet.rpc.spring.consumer.properties.BulletConsumerProperties;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreRequestFilter;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreResponseFilter;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.decorator.RequestInvokeDecoratorBuilder;
import com.smileframework.bullet.rpc.consumer.invoke.log.ConsumerRequestLogFilter;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(BulletConsumerRegistrar.class)
@EnableConfigurationProperties(BulletConsumerProperties.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(BulletCommonAutoConfig.class)
public class BulletConsumerAutoConfig {


    @Bean
    @ConditionalOnMissingBean(ServerConnectionManager.class)
    public ServerConnectionManager serverConnectionManager() {
        ServerConnectionManager serverConnectionManager = new ServerConnectionManager();
        return serverConnectionManager;
    }

    @Bean
    public SpringBulletConsumerContext bulletConsumerContext(BulletRpcConfig config,
                                                             BulletConsumerProperties consumerProperties,
                                                             ContentConvertManager contentConvertManager,
                                                             ServerConnectionManager serverConnectionManager) {
        SpringBulletConsumerContext consumerContext = new SpringBulletConsumerContext(consumerProperties, contentConvertManager, serverConnectionManager);
        if (config.consumer().getHandshakeInfoProvider() != null) {
            consumerContext.setHandshakeInfoProvider(config.consumer().getHandshakeInfoProvider());
        }
        if (config.consumer().getResponseErrorHandler() != null) {
            consumerContext.setResponseErrorHandler(config.consumer().getResponseErrorHandler());
        }
        for (ConsumerPreRequestFilter consumerPreRequestFilter : config.consumer().getConsumerPreRequestFilters()) {
            consumerContext.addPreRequestFilter(consumerPreRequestFilter);
        }
        for (ConsumerPreResponseFilter consumerPreResponseFilter : config.consumer().getConsumerPreResponseFilters()) {
            consumerContext.addPreResponseFilter(consumerPreResponseFilter);
        }
        for (RequestInvokeDecoratorBuilder decoratorBuilder : config.consumer().getRequestInvokeDecoratorBuilders()) {
            consumerContext.addRequestInvokeDecoratorBuilder(decoratorBuilder);
        }
        for (BroadcastRequestInvokerBuilder broadcastRequestInvokerBuilder : config.consumer().getBroadcastRequestInvokerBuilders()) {
            consumerContext.addBroadcastRequestInvokerBuilder(broadcastRequestInvokerBuilder);
        }
        if (consumerProperties.getEnableRequestLog()) {
            consumerContext.addPreResponseFilter(new ConsumerRequestLogFilter());
        }
        return consumerContext;
    }


}
