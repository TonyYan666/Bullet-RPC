package com.smileframework.bullet.spring.cloud.consumer.handshake;

import com.smileframework.bullet.transport.client.connection.handshake.HandshakeInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.util.Map;

public class SpringCloudHandshakeInfoProvider implements HandshakeInfoProvider {

    @Autowired
    private Registration registration;

    @Override
    public String instanceId() {
        return this.registration.getInstanceId();
    }

    @Override
    public String serviceId() {
        return this.registration.getServiceId();
    }

    @Override
    public String authorization() {
        return null;
    }

    @Override
    public Map<String, String> attributes() {
        return null;
    }
}
