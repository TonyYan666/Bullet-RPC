package com.smileframework.bullet.rpc.spring.provider.bean.factory;

import com.smileframework.bullet.rpc.spring.common.exception.ProviderCreatedException;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Constructor;

/**
 * 服务提供者bean工厂
 */
public class ServiceProviderBeanFactory implements FactoryBean {

    private Class serviceProviderClz;

    public ServiceProviderBeanFactory(Class serviceProviderClz) {
        this.serviceProviderClz = serviceProviderClz;
    }

    @Override
    public Object getObject() throws Exception {
        Object providerBean = null;
        try {
            Constructor constructor = this.serviceProviderClz.getDeclaredConstructor(null);
            providerBean = constructor.newInstance(null);
        } catch (Exception e) {
            throw new ProviderCreatedException("Server provider " + this.serviceProviderClz.getName() +
                    " could not be created. Service provider must has null arguments constructor.", e);
        }
        return providerBean;
    }

    @Override
    public Class<?> getObjectType() {
        return this.serviceProviderClz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
