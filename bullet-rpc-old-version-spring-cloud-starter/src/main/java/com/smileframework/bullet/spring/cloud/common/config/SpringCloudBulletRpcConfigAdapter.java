package com.smileframework.bullet.spring.cloud.common.config;

import com.smileframework.bullet.rpc.spring.common.config.BulletRpcConfig;
import com.smileframework.bullet.rpc.spring.common.config.adapter.BulletConfigAdapter;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerBuilder;
import com.smileframework.bullet.spring.cloud.consumer.handshake.SpringCloudHandshakeInfoProvider;
import com.smileframework.bullet.spring.cloud.consumer.invoke.LoadBalancerRequestInvokerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SpringCloudBulletRpcConfigAdapter implements BulletConfigAdapter {


    @Autowired
    private SpringCloudHandshakeInfoProvider springCloudHandshakeInfoProvider;

    @Autowired
    @Qualifier("loadBalancerRequestInvokerBuilder")
    private LoadBalancerRequestInvokerBuilder loadBalancerRequestInvokerBuilder;


    @Autowired
    @Qualifier("discoveryBroadcastRequestInvokerBuilder")
    private BroadcastRequestInvokerBuilder discoveryBroadcastRequestInvokerBuilder;


    @Override
    public void config(BulletRpcConfig config) {
        if (config.consumer().getHandshakeInfoProvider() == null) {
            config.consumer().setHandshakeInfoProvider(this.springCloudHandshakeInfoProvider);
        }
        config.consumer().addRequestInvokeDecoratorBuilder(this.loadBalancerRequestInvokerBuilder);
        config.consumer().addBroadcastRequestInvokerBuilder(this.discoveryBroadcastRequestInvokerBuilder);
    }
}
