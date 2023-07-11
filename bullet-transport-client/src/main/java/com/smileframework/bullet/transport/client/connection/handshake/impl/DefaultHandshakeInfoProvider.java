package com.smileframework.bullet.transport.client.connection.handshake.impl;

import com.smileframework.bullet.transport.client.connection.handshake.HandshakeInfoProvider;

import java.util.Map;
import java.util.UUID;

public class DefaultHandshakeInfoProvider implements HandshakeInfoProvider {

    @Override
    public String instanceId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String serviceId() {
        return "Consumer";
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
