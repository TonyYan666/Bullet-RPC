package com.smileframework.bullet.spring.cloud.consumer.connection;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.spring.cloud.common.constant.BulletCloudConstant;
import com.smileframework.bullet.transport.client.connection.ServerConnectionManager;
import com.smileframework.bullet.transport.client.connection.dto.AddressAndPort;
import com.smileframework.bullet.transport.common.constant.BulletConstant;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientConnectException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.Request;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SpringCloudServerConnectionManager extends ServerConnectionManager {

    private LoadBalancerClient loadBalancerClient;

    private DiscoveryClient discoveryClient;

    public SpringCloudServerConnectionManager(LoadBalancerClient loadBalancerClient, DiscoveryClient discoveryClient) {
        super();
        this.loadBalancerClient = loadBalancerClient;
        this.discoveryClient = discoveryClient;
    }

    @Override
    protected AddressAndPort getRealAddressAndPort(URI originalUri, BulletRequest<?> request) {
        String serviceId = originalUri.getHost();
        if (Validator.isIpv4(serviceId)) {
            return super.getRealAddressAndPort(originalUri, request);
        }
        ServiceInstance serviceInstance = this.chooseServiceInstance(serviceId, request);
        if (serviceInstance == null) {
            int port = originalUri.getPort();
            if (port <= 0) {
                port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
            }
            return new AddressAndPort(originalUri.getHost(), port);
        }
        return this.getAddressAndPortByServiceInstance(serviceInstance);
    }

    @Override
    protected List<AddressAndPort> getRealAddressAndPortList(URI originalUri, BulletRequest<?> request) {
        List<AddressAndPort> addressAndPorts = new ArrayList<>();
        List<ServiceInstance> serviceInstances = this.discoveryClient.getInstances(originalUri.getHost());
        if (CollectionUtil.isEmpty(serviceInstances)) {
            throw new BulletClientConnectException("[Bullet-Spring-Cloud] service name " + originalUri.getHost() + " is not available service instance.");
        }
        for (ServiceInstance serviceInstance : serviceInstances) {
            AddressAndPort addressAndPort = this.getAddressAndPortByServiceInstance(serviceInstance);
            addressAndPorts.add(addressAndPort);
        }
        return addressAndPorts;
    }

    @Override
    protected List<AddressAndPort> getAllServersAddressAndPort(BulletRequest<?> request) {
        List<AddressAndPort> addressAndPorts = new ArrayList<>();
        for (String service : this.discoveryClient.getServices()) {
            addressAndPorts.addAll(this.getRealAddressAndPortList(URI.create("bullet://" + service), request));
        }
        return addressAndPorts;
    }

    protected ServiceInstance chooseServiceInstance(String serviceId, BulletRequest<?> request) {
        Request<BulletRequest<?>> loadBalancerRequest = new DefaultRequest<>(request);
        ServiceInstance serviceInstance = this.loadBalancerClient.choose(serviceId, loadBalancerRequest);
        return serviceInstance;
    }

    private AddressAndPort getAddressAndPortByServiceInstance(ServiceInstance serviceInstance) {
        int port = BulletConstant.BULLET_SERVER_DEFAULT_PORT;
        if (serviceInstance.getMetadata().containsKey(BulletCloudConstant.DISCOVERY_META_PORT)) {
            String portStr = serviceInstance.getMetadata().get(BulletCloudConstant.DISCOVERY_META_PORT);
            if (StrUtil.isNotBlank(portStr)) {
                port = Integer.valueOf(portStr);
            }
        }
        return new AddressAndPort(serviceInstance.getHost(), port);
    }


}
