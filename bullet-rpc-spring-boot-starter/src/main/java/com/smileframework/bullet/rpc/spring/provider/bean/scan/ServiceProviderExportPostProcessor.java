package com.smileframework.bullet.rpc.spring.provider.bean.scan;

import com.smileframework.bullet.rpc.provider.BulletProviderContext;
import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * 已废弃，改用 applicationContext 方式去获得ServiceProvider去注册
 * 具体注册代码 com.smileframework.bullet.rpc.spring.provider.config.BulletProviderAutoConfig
 */
@Deprecated
public class ServiceProviderExportPostProcessor implements BeanPostProcessor, Ordered {

    @Autowired
    private BulletProviderContext bulletProviderContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().getAnnotation(ServiceProvider.class) != null) {
            this.bulletProviderContext.providerRegister(bean);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
