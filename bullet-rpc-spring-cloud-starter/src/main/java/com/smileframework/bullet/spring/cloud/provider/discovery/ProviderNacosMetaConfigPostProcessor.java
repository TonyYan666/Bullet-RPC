package com.smileframework.bullet.spring.cloud.provider.discovery;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.smileframework.bullet.rpc.spring.provider.properties.BulletProviderProperties;
import com.smileframework.bullet.spring.cloud.common.constant.BulletCloudConstant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ProviderNacosMetaConfigPostProcessor implements BeanPostProcessor {

    @Autowired
    private BulletProviderProperties providerProperties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof NacosRegistration) {
            NacosRegistration nacosRegistration = (NacosRegistration) bean;
            nacosRegistration.getMetadata().put(BulletCloudConstant.DISCOVERY_META_PORT, String.valueOf(this.providerProperties.getPort()));
        }
        return bean;
    }
}
