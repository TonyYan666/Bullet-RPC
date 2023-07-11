package com.smileframework.bullet.spring.cloud.provider.discovery;

import com.smileframework.bullet.rpc.spring.provider.properties.BulletProviderProperties;
import com.smileframework.bullet.spring.cloud.common.constant.BulletCloudConstant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

public class ProviderEurekaMetaConfigPostProcessor implements BeanPostProcessor {

    @Autowired
    private BulletProviderProperties providerProperties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EurekaInstanceConfigBean) {
            EurekaInstanceConfigBean configBean = (EurekaInstanceConfigBean) bean;
            configBean.getMetadataMap().put(BulletCloudConstant.DISCOVERY_META_PORT, String.valueOf(this.providerProperties.getPort()));
        }
        return bean;
    }
}
