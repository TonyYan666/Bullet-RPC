package com.smileframework.bullet.rpc.spring.consumer.fallback;

import com.smileframework.bullet.rpc.consumer.invoke.fallback.factory.FallbackHandlerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringFallbackHandlerFactory implements FallbackHandlerFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public <T> T getFallbackHandler(Class fallbackClz) {
        return (T) this.applicationContext.getBean(fallbackClz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
