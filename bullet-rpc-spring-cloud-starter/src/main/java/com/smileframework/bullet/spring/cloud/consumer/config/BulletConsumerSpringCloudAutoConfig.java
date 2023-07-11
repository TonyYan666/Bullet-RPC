package com.smileframework.bullet.spring.cloud.consumer.config;

import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvokerBuilder;
import com.smileframework.bullet.rpc.spring.consumer.config.BulletConsumerAutoConfig;
import com.smileframework.bullet.spring.cloud.common.config.SpringCloudBulletRpcConfigAdapter;
import com.smileframework.bullet.spring.cloud.consumer.handshake.SpringCloudHandshakeInfoProvider;
import com.smileframework.bullet.spring.cloud.consumer.invoke.LoadBalancerRequestInvokerBuilder;
import com.smileframework.bullet.spring.cloud.consumer.invoke.SpringCloudBroadcastRequestInvokerBuilder;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BulletConsumerAutoConfig.class)
public class BulletConsumerSpringCloudAutoConfig {

    @Bean
    @ConditionalOnMissingBean(name = "loadBalancerRequestInvokerBuilder")
    public LoadBalancerRequestInvokerBuilder loadBalancerRequestInvokerBuilder(LoadBalancerClientFactory loadBalancerClientFactory, LoadBalancerClient loadBalancerClient) {
        return new LoadBalancerRequestInvokerBuilder(loadBalancerClientFactory, loadBalancerClient);
    }

    @Bean
    @ConditionalOnMissingBean(name = "discoveryBroadcastRequestInvokerBuilder")
    public BroadcastRequestInvokerBuilder discoveryBroadcastRequestInvokerBuilder(DiscoveryClient discoveryClient) {
        return new SpringCloudBroadcastRequestInvokerBuilder(discoveryClient);
    }


    @Bean
    public SpringCloudHandshakeInfoProvider handshakeInfoProvider() {
        SpringCloudHandshakeInfoProvider handshakeInfoProvider = new SpringCloudHandshakeInfoProvider();
        return handshakeInfoProvider;
    }


    @Bean
    public SpringCloudBulletRpcConfigAdapter springCloudBulletRpcConfigAdapter() {
        return new SpringCloudBulletRpcConfigAdapter();
    }


//    @Bean
//    @ConditionalOnMissingBean(ServerConnectionManager.class)
//    public SpringCloudServerConnectionManager serverConnectionManager(LoadBalancerClient loadBalancerClient, DiscoveryClient discoveryClient) {
//        SpringCloudServerConnectionManager springCloudServerConnectionManager = new SpringCloudServerConnectionManager(loadBalancerClient, discoveryClient);
//        return springCloudServerConnectionManager;
//    }

}
