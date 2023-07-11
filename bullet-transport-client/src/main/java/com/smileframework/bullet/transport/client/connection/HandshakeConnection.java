package com.smileframework.bullet.transport.client.connection;

import com.smileframework.bullet.transport.client.connection.handshake.HandshakeInfoProvider;
import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import com.smileframework.bullet.transport.common.exception.connection.CheckServerConnectionException;
import com.smileframework.bullet.transport.common.exception.handshake.ConnectionHandshakeException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class HandshakeConnection extends CommunicableConnection {

    /**
     * 连接握手信息提供者
     */
    private HandshakeInfoProvider handshakeInfoProvider;

    public HandshakeConnection(ServerConnectionProperties properties) {
        super(properties);
    }

    protected void setHandshakeInfoProvider(HandshakeInfoProvider handshakeInfoProvider) {
        this.handshakeInfoProvider = handshakeInfoProvider;
    }

    /**
     * 连接握手
     */
    public void connectionHandshake() {
        if (this.handshakeInfoProvider == null) {
            return;
        }
        ConnectionHandshake connectionHandshake = new ConnectionHandshake();
        connectionHandshake.setAuthorization(this.handshakeInfoProvider.authorization());
        connectionHandshake.setServiceId(this.handshakeInfoProvider.serviceId());
        connectionHandshake.setInstanceId(this.handshakeInfoProvider.instanceId());
        connectionHandshake.setAttributes(this.handshakeInfoProvider.attributes());
        BulletRequest<ConnectionHandshake> request = BulletRequest.createHandshakeRequest();
        request.setPayload(connectionHandshake);
        try {
            this.sendAndReturnObject(request, this.properties.getHandshakeTimeout(), Void.class);
        } catch (ExecutionException e) {
            this.disconnect();
            throw new ConnectionHandshakeException("Bullet client handshake execution error.", e);
        } catch (InterruptedException e) {
            this.disconnect();
            throw new ConnectionHandshakeException("Bullet client handshake interrupted error.", e);
        } catch (TimeoutException e) {
            this.disconnect();
            throw new ConnectionHandshakeException("Bullet client handshake timeout error.", e);
        } catch (Exception e) {
            this.disconnect();
            throw new ConnectionHandshakeException("Bullet client handshake failure.", e);
        }
    }

    @Override
    protected void checkBeforeConnect() {
        super.checkBeforeConnect();
        if (this.handshakeInfoProvider == null) {
            throw new CheckServerConnectionException("handshake info provider didn't set.");
        }
    }
}


