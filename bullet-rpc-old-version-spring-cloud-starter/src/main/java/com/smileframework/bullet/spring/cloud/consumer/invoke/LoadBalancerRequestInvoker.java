package com.smileframework.bullet.spring.cloud.consumer.invoke;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.spring.cloud.common.constant.BulletCloudConstant;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.common.constant.BulletConstant;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

public class LoadBalancerRequestInvoker implements ConsumerRequestInvoker {

    private final LoadBalancerClient loadBalancerClient;

    private final ConsumerRequestInvoker requestInvoker;

    public LoadBalancerRequestInvoker(LoadBalancerClient loadBalancerClient, ConsumerRequestInvoker requestInvoker) {
        this.loadBalancerClient = loadBalancerClient;
        this.requestInvoker = requestInvoker;
    }

    @Override
    public BulletResponse<Object> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        if (Validator.isIpv4(request.getHeader().getServerAddress().getHost())) {
            return this.requestInvoker.invoke(methodDefinition, request);
        }
        String uri = "bullet://" + request.getHeader().getServerAddress().getHost() + request.getHeader().getActionURL();
        final URI originalUri = URI.create(uri);
        String serviceId = originalUri.getHost();
        Request<BulletRequest<Object[]>> defaultRequest = null;
        defaultRequest = new DefaultRequest<>(request);
        final Request<BulletRequest<Object[]>> lbRequest = defaultRequest;
        ServiceInstance retrievedServiceInstance = loadBalancerClient.choose(serviceId);
        if (retrievedServiceInstance != null) {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (retrievedServiceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = retrievedServiceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            request.getHeader().setServerAddress(URI.create("bullet://" + retrievedServiceInstance.getHost() + ":" + port));
        }
        BulletResponse<Object> response = this.requestInvoker.invoke(methodDefinition, request);
        return response;
    }

    @Override
    public BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> genericFutureListeners) {
        if (Validator.isIpv4(request.getHeader().getServerAddress().getHost())) {
            return this.requestInvoker.asyncInvoke(methodDefinition, request, genericFutureListeners);
        }
        String uri = "bullet://" + request.getHeader().getServerAddress().getHost() + request.getHeader().getActionURL();
        final URI originalUri = URI.create(uri);
        String serviceId = originalUri.getHost();
        Request<BulletRequest<Object[]>> defaultRequest = null;
        defaultRequest = new DefaultRequest<>(request);
        final Request<BulletRequest<Object[]>> lbRequest = defaultRequest;
        ServiceInstance retrievedServiceInstance = loadBalancerClient.choose(serviceId);
        if (retrievedServiceInstance != null) {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (retrievedServiceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = retrievedServiceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            request.getHeader().setServerAddress(URI.create("bullet://" + retrievedServiceInstance.getHost() + ":" + port));
        }
        return this.requestInvoker.asyncInvoke(methodDefinition, request, genericFutureListeners);
    }

    @Override
    public Mono<BulletResponse<Object>> reactiveInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        if (Validator.isIpv4(request.getHeader().getServerAddress().getHost())) {
            return this.requestInvoker.reactiveInvoke(methodDefinition, request);
        }
        String uri = "bullet://" + request.getHeader().getServerAddress().getHost() + request.getHeader().getActionURL();
        final URI originalUri = URI.create(uri);
        String serviceId = originalUri.getHost();
        ServiceInstance retrievedServiceInstance = loadBalancerClient.choose(serviceId);
        if (retrievedServiceInstance != null) {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (retrievedServiceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = retrievedServiceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            request.getHeader().setServerAddress(URI.create("bullet://" + retrievedServiceInstance.getHost() + ":" + port));
        }
        return this.requestInvoker.reactiveInvoke(methodDefinition, request);
    }
}
