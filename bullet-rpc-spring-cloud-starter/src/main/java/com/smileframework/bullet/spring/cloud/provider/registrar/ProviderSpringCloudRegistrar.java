package com.smileframework.bullet.spring.cloud.provider.registrar;

import com.smileframework.bullet.rpc.spring.common.util.BeanRegistrationUtil;
import com.smileframework.bullet.spring.cloud.provider.discovery.ProviderEurekaMetaConfigPostProcessor;
import com.smileframework.bullet.spring.cloud.provider.discovery.ProviderNacosMetaConfigPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ProviderSpringCloudRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            if (Class.forName("org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean") != null) {
                BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ProviderEurekaMetaConfigPostProcessor.class.getName(), ProviderEurekaMetaConfigPostProcessor.class);
            }
        } catch (ClassNotFoundException e) {
            //pass
        }
        try {
            if (Class.forName("com.alibaba.cloud.nacos.registry.NacosRegistration") != null) {
                BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ProviderNacosMetaConfigPostProcessor.class.getName(), ProviderNacosMetaConfigPostProcessor.class);
            }
        } catch (ClassNotFoundException e) {
            //pass
        }
    }
}
