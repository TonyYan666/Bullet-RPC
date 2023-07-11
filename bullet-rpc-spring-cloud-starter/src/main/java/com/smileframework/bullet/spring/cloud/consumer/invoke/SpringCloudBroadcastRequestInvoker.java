package com.smileframework.bullet.spring.cloud.consumer.invoke;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.ConsumerRequestInvoker;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastAsyncInvokerCallback;
import com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.BroadcastRequestInvoker;
import com.smileframework.bullet.spring.cloud.common.constant.BulletCloudConstant;
import com.smileframework.bullet.transport.common.constant.BulletConstant;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.util.*;

public class SpringCloudBroadcastRequestInvoker implements BroadcastRequestInvoker {

    private DiscoveryClient discoveryClient;

    private final ConsumerRequestInvoker requestInvoker;

    public SpringCloudBroadcastRequestInvoker(DiscoveryClient discoveryClient, ConsumerRequestInvoker requestInvoker) {
        this.discoveryClient = discoveryClient;
        this.requestInvoker = requestInvoker;
    }

    @Override
    public Map<String, BulletResponse> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request) {
        Map<String, BulletResponse> bulletResponseMap = new HashMap<>();
        if (Validator.isIpv4(request.getHeader().getServerAddress().getHost())) {
            BulletResponse bulletResponse = this.requestInvoker.invoke(methodDefinition, request);
            bulletResponseMap.put(request.getHeader().getServerAddress().getHost(), bulletResponse);
            return bulletResponseMap;
        }

        String uri = "bullet://" + request.getHeader().getServerAddress().getHost() + request.getHeader().getActionURL();
        final URI originalUri = URI.create(uri);
        String serviceId = originalUri.getHost();
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
        if (CollectionUtil.isEmpty(instances)) {
            BulletResponse bulletResponse = this.requestInvoker.invoke(methodDefinition, request);
            bulletResponseMap.put(request.getHeader().getServerAddress().getHost(), bulletResponse);
            return bulletResponseMap;
        }

        for (ServiceInstance instance : instances) {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (instance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = instance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            BulletRequest<Object[]> proxyRequest = (BulletRequest<Object[]>) request.clone();
            proxyRequest.getHeader().setServerAddress(URI.create("bullet://" + instance.getHost() + ":" + port));
            BulletResponse<Object> response = this.requestInvoker.invoke(methodDefinition, request);
            if (StrUtil.isNotBlank(instance.getInstanceId())) {
                bulletResponseMap.put(instance.getInstanceId(), response);
            } else {
                bulletResponseMap.put(instance.getHost() + ":" + port, response);
            }
        }
        return bulletResponseMap;
    }

    @Override
    public <T> Map<String, T> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BroadcastAsyncInvokerCallback<T> callback) {
        Map<String, T> resultMap = new HashMap<>();
        if (Validator.isIpv4(request.getHeader().getServerAddress().getHost())) {
            List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners = new ArrayList<>();
            T result = callback.callback(listeners);
            resultMap.put(request.getHeader().getServerAddress().getHost(), result);
            this.requestInvoker.asyncInvoke(methodDefinition, request, listeners);
            return resultMap;
        }
        String uri = "bullet://" + request.getHeader().getServerAddress().getHost() + request.getHeader().getActionURL();
        final URI originalUri = URI.create(uri);
        String serviceId = originalUri.getHost();
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
        if (CollectionUtil.isEmpty(instances)) {
            List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners = new ArrayList<>();
            T result = callback.callback(listeners);
            resultMap.put(request.getHeader().getServerAddress().getHost(), result);
            this.requestInvoker.asyncInvoke(methodDefinition, request, listeners);
            return resultMap;
        }

        for (ServiceInstance instance : instances) {
            int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            if (instance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
                String portStr = instance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
                if (StrUtil.isNotBlank(portStr)) {
                    port = Integer.valueOf(portStr);
                }
            }
            BulletRequest<Object[]> proxyRequest = (BulletRequest<Object[]>) request.clone();
            proxyRequest.getHeader().setServerAddress(URI.create("bullet://" + instance.getHost() + ":" + port));
            List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners = new ArrayList<>();
            T result = callback.callback(listeners);
            if (StrUtil.isNotBlank(instance.getInstanceId())) {
                resultMap.put(instance.getInstanceId(), result);
            } else {
                resultMap.put(instance.getHost() + ":" + port, result);
            }
            this.requestInvoker.asyncInvoke(methodDefinition, proxyRequest, listeners);
        }
        return resultMap;

    }


}
