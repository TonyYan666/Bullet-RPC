package com.smileframework.bullet.rpc.spring.common.config;

import cn.hutool.core.collection.CollectionUtil;
import com.smileframework.bullet.rpc.spring.common.annotation.ScanBulletConsumer;
import com.smileframework.bullet.rpc.spring.common.annotation.ScanBulletProvider;
import com.smileframework.bullet.rpc.spring.common.config.adapter.BulletConfigAdapter;
import com.smileframework.bullet.rpc.spring.consumer.fallback.SpringFallbackHandlerFactory;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.common.protocol.serialization.handler.ContentConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class BulletCommonAutoConfig {

    @Bean
    public SpringFallbackHandlerFactory springFallbackHandlerFactory() {
        return new SpringFallbackHandlerFactory();
    }

    @Bean
    public SpringBootBulletConfigAdapter springBootBulletConfigAdapter(SpringFallbackHandlerFactory springFallbackHandlerFactory) {
        SpringBootBulletConfigAdapter springBootBulletConfigAdapter = new SpringBootBulletConfigAdapter(springFallbackHandlerFactory);
        return springBootBulletConfigAdapter;
    }

    @Bean
    public BulletRpcConfig bulletRpcConfig(@Autowired(required = false) List<BulletConfigAdapter> adapters) {
        BulletRpcConfig config = new BulletRpcConfig();
        if (adapters != null && adapters.size() > 0) {
            for (BulletConfigAdapter adapter : adapters) {
                if (adapter != null) {
                    adapter.config(config);
                }
            }
        }
        config.configured();
        return config;
    }

    @Bean
    public ContentConvertManager contentConvertManager(BulletRpcConfig bulletRpcConfig) {
        ContentConvertManager contentConvertManager = new ContentConvertManager();
        for (ContentConvertor contentConvertor : bulletRpcConfig.getContentConvertors()) {
            contentConvertManager.replaceContentConvertors(contentConvertor);
        }
        return contentConvertManager;
    }

    @Bean
    public BulletScanPackageConfig bulletScanPackageConfig(ApplicationContext applicationContext) {
        BulletScanPackageConfig bulletScanPackageConfig = new BulletScanPackageConfig();
        Map<String, Object> consumerScanConfigBean = applicationContext.getBeansWithAnnotation(ScanBulletConsumer.class);
        Set<String> consumerPackages = new HashSet<>();
        if (!CollectionUtil.isEmpty(consumerScanConfigBean)) {
            for (Object bean : consumerScanConfigBean.values()) {
                ScanBulletConsumer consumer = bean.getClass().getAnnotation(ScanBulletConsumer.class);
                for (String packageStr : consumer.packages()) {
                    consumerPackages.add(packageStr);
                }
            }
        }
        Set<String> providerPackages = new HashSet<>();
        Map<String, Object> providerScanConfig = applicationContext.getBeansWithAnnotation(ScanBulletProvider.class);
        if (!CollectionUtil.isEmpty(providerScanConfig)) {
            for (Object bean : providerScanConfig.values()) {
                ScanBulletProvider provider = bean.getClass().getAnnotation(ScanBulletProvider.class);
                for (String packageStr : provider.packages()) {
                    providerPackages.add(packageStr);
                }
            }
        }
        bulletScanPackageConfig.setConsumerPackages(consumerPackages);
        bulletScanPackageConfig.setProviderPackages(providerPackages);
        return bulletScanPackageConfig;
    }

}
