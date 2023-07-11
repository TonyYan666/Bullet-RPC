package com.smileframework.bullet.rpc.spring.common.config;

import com.smileframework.bullet.rpc.spring.common.config.adapter.BulletConfigAdapter;
import com.smileframework.bullet.rpc.spring.consumer.fallback.SpringFallbackHandlerFactory;

public class SpringBootBulletConfigAdapter implements BulletConfigAdapter {

    private SpringFallbackHandlerFactory springFallbackHandlerFactory;

    public SpringBootBulletConfigAdapter(SpringFallbackHandlerFactory springFallbackHandlerFactory) {
        this.springFallbackHandlerFactory = springFallbackHandlerFactory;
    }

    @Override
    public void config(BulletRpcConfig config) {
        config.consumer().setFallbackHandlerFactory(this.springFallbackHandlerFactory);
    }
}
