package com.smileframework.bullet.rpc.spring.provider.config;

import cn.hutool.core.collection.CollectionUtil;
import com.smileframework.bullet.rpc.spring.common.config.BulletCommonAutoConfig;
import com.smileframework.bullet.rpc.spring.common.config.BulletRpcConfig;
import com.smileframework.bullet.rpc.spring.provider.SpringBulletProviderContext;
import com.smileframework.bullet.rpc.spring.provider.bean.registrar.BulletProviderRegistrar;
import com.smileframework.bullet.rpc.spring.provider.properties.BulletProviderProperties;
import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProvider;
import com.smileframework.bullet.rpc.provider.invoker.interceptor.ProviderInvokeInterceptor;
import com.smileframework.bullet.rpc.provider.invoker.log.ProviderInvokeLoggerInterceptor;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.server.connection.handshake.HandshakeEventListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Import(BulletProviderRegistrar.class)
@EnableConfigurationProperties(BulletProviderProperties.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(BulletCommonAutoConfig.class)
public class BulletProviderAutoConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Bean
    public SpringBulletProviderContext bulletProviderContext(BulletProviderProperties providerProperties, BulletRpcConfig bulletRpcConfig, ContentConvertManager contentConvertManager) {
        SpringBulletProviderContext providerContext = new SpringBulletProviderContext();
        providerContext.init(providerProperties, contentConvertManager);
        if (bulletRpcConfig.provider().getInvokeErrorTranslator() != null) {
            providerContext.setInvokeErrorTranslator(bulletRpcConfig.provider().getInvokeErrorTranslator());
        }
        if (bulletRpcConfig.provider().getConnectionAuthenticationManager() != null) {
            providerContext.setConnectionAuthenticationManager(bulletRpcConfig.provider().getConnectionAuthenticationManager());
        }
        for (ProviderInvokeInterceptor providerInvokeInterceptor : bulletRpcConfig.provider().getProviderInvokeInterceptors()) {
            providerContext.addInterceptor(providerInvokeInterceptor);
        }
        for (HandshakeEventListener handshakeEventListener : bulletRpcConfig.provider().getHandshakeEventListeners()) {
            providerContext.addHandshakeEventListener(handshakeEventListener);
        }
        if (providerProperties.getEnableInvokeLog()) {
            providerContext.addInterceptor(new ProviderInvokeLoggerInterceptor());
        }
        Map<String, Object> providerBeans = this.applicationContext.getBeansWithAnnotation(ServiceProvider.class);
        if (CollectionUtil.isNotEmpty(providerBeans)) {
            for (Object provider : providerBeans.values()) {
                providerContext.providerRegister(provider, AopUtils.getTargetClass(provider));
            }
        }
        return providerContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
