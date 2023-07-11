package com.smileframework.bullet.spring.cloud.consumer.invoke;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.spring.cloud.common.constant.BulletCloudConstant;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.common.constant.BulletConstant;
import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderBusyException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LoadBalancerRequestInvoker implements ConsumerRequestInvoker {

    private final LoadBalancerClientFactory loadBalancerClientFactory;

    private final LoadBalancerClient loadBalancerClient;

    private final ConsumerRequestInvoker requestInvoker;

    public LoadBalancerRequestInvoker(LoadBalancerClientFactory loadBalancerClientFactory, LoadBalancerClient loadBalancerClient, ConsumerRequestInvoker requestInvoker) {
        this.loadBalancerClientFactory = loadBalancerClientFactory;
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
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(
                        loadBalancerClientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RetryableRequestContext.class, ResponseData.class, ServiceInstance.class);
        Request<BulletRequest<Object[]>> defaultRequest = null;
        defaultRequest = new DefaultRequest<>(request);
        final Request<BulletRequest<Object[]>> lbRequest = defaultRequest;
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
        ServiceInstance retrievedServiceInstance = loadBalancerClient.choose(serviceId, lbRequest);
        if (retrievedServiceInstance == null) {
            Response<ServiceInstance> lbResponse = new DefaultResponse(
                    retrievedServiceInstance);
            supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                    .onComplete(new CompletionContext<ResponseData, ServiceInstance, BulletRequest<Object[]>>(
                            CompletionContext.Status.DISCARD, lbRequest, lbResponse)));
        } else {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (retrievedServiceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = retrievedServiceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            request.getHeader().setServerAddress(URI.create("bullet://" + retrievedServiceInstance.getHost() + ":" + port));
        }
        Response<ServiceInstance> lbResponse = new DefaultResponse(
                retrievedServiceInstance);
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, lbResponse));
        try {
            BulletResponse<Object> response = this.requestInvoker.invoke(methodDefinition, request);
            supportedLifecycleProcessors.forEach(
                    lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
                            lbRequest, lbResponse, response)));
            return response;
        } catch (Exception exception) {
            if (exception instanceof BulletProviderBusyException) {
                supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
                        new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, lbResponse)));
            }
            throw exception;
        }
    }

    @Override
    public BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> genericFutureListeners) {
        if (Validator.isIpv4(request.getHeader().getServerAddress().getHost())) {
            return this.requestInvoker.asyncInvoke(methodDefinition, request, genericFutureListeners);
        }
        String uri = "bullet://" + request.getHeader().getServerAddress().getHost() + request.getHeader().getActionURL();
        final URI originalUri = URI.create(uri);
        String serviceId = originalUri.getHost();
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(
                        loadBalancerClientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RetryableRequestContext.class, ResponseData.class, ServiceInstance.class);
        Request<BulletRequest<Object[]>> defaultRequest = null;
        defaultRequest = new DefaultRequest<>(request);
        final Request<BulletRequest<Object[]>> lbRequest = defaultRequest;
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
        ServiceInstance retrievedServiceInstance = loadBalancerClient.choose(serviceId, lbRequest);
        if (retrievedServiceInstance == null) {
            Response<ServiceInstance> lbResponse = new DefaultResponse(
                    retrievedServiceInstance);
            supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                    .onComplete(new CompletionContext<ResponseData, ServiceInstance, BulletRequest<Object[]>>(
                            CompletionContext.Status.DISCARD, lbRequest, lbResponse)));
        } else {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (retrievedServiceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = retrievedServiceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            request.getHeader().setServerAddress(URI.create("bullet://" + retrievedServiceInstance.getHost() + ":" + port));
        }
        Response<ServiceInstance> lbResponse = new DefaultResponse(
                retrievedServiceInstance);
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, lbResponse));
        GenericFutureListener<Future<? super BulletResponse<Object>>> loadbalancerLifecycleMonitorListener = internalFuture -> {
            try {
                BulletResponse<Object> response = (BulletResponse<Object>) internalFuture.get();
                supportedLifecycleProcessors.forEach(
                        lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
                                lbRequest, lbResponse, response)));
            } catch (Exception exception) {
                if (exception instanceof BulletProviderBusyException) {
                    supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
                            new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, lbResponse)));
                }
            }
        };
        if (genericFutureListeners == null) {
            genericFutureListeners = new ArrayList<>();
        }
        genericFutureListeners.add(loadbalancerLifecycleMonitorListener);
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
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(
                        loadBalancerClientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RetryableRequestContext.class, ResponseData.class, ServiceInstance.class);
        Request<BulletRequest<Object[]>> defaultRequest = null;
        defaultRequest = new DefaultRequest<>(request);
        final Request<BulletRequest<Object[]>> lbRequest = defaultRequest;
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
        ReactiveLoadBalancer<ServiceInstance> loadBalancer = this.loadBalancerClientFactory.getInstance(serviceId);
        if (loadBalancer == null) {
            return this.requestInvoker.reactiveInvoke(methodDefinition, request);
        }
        return Mono.from(loadBalancer.choose(lbRequest)).flatMap(lbResponse -> {
            if (!lbResponse.hasServer()) {
                supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, BulletRequest<Object[]>>(
                                CompletionContext.Status.DISCARD, lbRequest, lbResponse)));
            } else {
                ServiceInstance retrievedServiceInstance = lbResponse.getServer();
                int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
                if (retrievedServiceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                    String portStr = retrievedServiceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                    if (StrUtil.isNotBlank(portStr)) {
                        port = Integer.valueOf(portStr);
                    }
                }
                request.getHeader().setServerAddress(URI.create("bullet://" + retrievedServiceInstance.getHost() + ":" + port));
            }
            supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, lbResponse));
            return this.requestInvoker.reactiveInvoke(methodDefinition, request)
                    .doOnSuccess(response -> {
                        supportedLifecycleProcessors.forEach(
                                lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
                                        lbRequest, lbResponse, response)));
                    })
                    .doOnError(exception -> {
                        if (exception instanceof BulletProviderBusyException) {
                            supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
                                    new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, lbResponse)));
                        }
                    });
        });
    }


}