package com.smileframework.bullet.spring.cloud.provider.registrar;

import com.smileframework.bullet.rpc.spring.common.util.BeanRegistrationUtil;
import com.smileframework.bullet.spring.cloud.provider.discovery.ProviderEurekaMetaConfigPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ProviderSpringCloudRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ProviderEurekaMetaConfigPostProcessor.class.getName(), ProviderEurekaMetaConfigPostProcessor.class);
    }
}
