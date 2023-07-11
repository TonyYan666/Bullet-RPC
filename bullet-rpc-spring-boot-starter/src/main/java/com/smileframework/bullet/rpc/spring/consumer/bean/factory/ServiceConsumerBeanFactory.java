package com.smileframework.bullet.rpc.spring.consumer.bean.factory;

import com.smileframework.bullet.rpc.consumer.BulletConsumerContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 服务提供者bean工厂
 */
public class ServiceConsumerBeanFactory implements FactoryBean {

    private Class serviceConsumerClz;

    @Autowired
    private BulletConsumerContext bulletConsumerContext;

    public ServiceConsumerBeanFactory(Class serviceConsumerClz) {
        this.serviceConsumerClz = serviceConsumerClz;
    }

    @Override
    public Object getObject() throws Exception {
        return this.bulletConsumerContext.getServiceConsumerProxy(this.serviceConsumerClz);
    }

    @Override
    public Class<?> getObjectType() {
        return this.serviceConsumerClz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
