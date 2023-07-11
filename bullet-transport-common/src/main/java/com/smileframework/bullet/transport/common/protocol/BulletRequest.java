package com.smileframework.bullet.transport.common.protocol;

import cn.hutool.core.lang.UUID;
import com.smileframework.bullet.transport.common.protocol.handshake.ConnectionHandshake;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 协议请求
 */
@Getter
@Setter
public class BulletRequest<T> implements Serializable, Cloneable {

    /**
     * 请求头信息
     */
    private BulletRequestHeader header;

    /**
     * 请求负载（参数或者是传输数据）
     */
    private T payload;

    public static BulletRequest<ConnectionHandshake> createHandshakeRequest() {
        BulletRequest<ConnectionHandshake> request = new BulletRequest<>();
        BulletRequestHeader header = new BulletRequestHeader();
        header.setRequestId(UUID.fastUUID().toString(true));
        header.setOperationType(BulletRequestHeader.OPERATION_TYPE_HANDSHAKE);
        request.setHeader(header);
        return request;
    }

    public static BulletRequest createHeartbeatRequest() {
        BulletRequest request = new BulletRequest();
        BulletRequestHeader header = new BulletRequestHeader();
        header.setRequestId(UUID.fastUUID().toString(true));
        header.setOperationType(BulletRequestHeader.OPERATION_TYPE_HEARTBEAT);
        request.setHeader(header);
        return request;
    }

    public static BulletRequest createActionRequest(String actionUrl) {
        BulletRequest request = new BulletRequest();
        BulletRequestHeader header = new BulletRequestHeader();
        header.setRequestId(UUID.fastUUID().toString(true));
        header.setOperationType(BulletRequestHeader.OPERATION_TYPE_ACTION);
        header.setActionURL(actionUrl);
        request.setHeader(header);
        return request;
    }

    public static BulletRequest createNotificationRequest(String actionUrl) {
        BulletRequest request = new BulletRequest();
        BulletRequestHeader header = new BulletRequestHeader();
        header.setRequestId(UUID.fastUUID().toString(true));
        header.setOperationType(BulletRequestHeader.OPERATION_TYPE_NOTIFICATION);
        header.setActionURL(actionUrl);
        request.setHeader(header);
        return request;
    }


    @Override
    public String toString() {
        return "BulletTransportRequest{" +
                "header=" + header +
                '}';
    }

    @Override
    public Object clone() {
        BulletRequest bulletRequest = new BulletRequest();
        bulletRequest.setPayload(this.payload);
        bulletRequest.setHeader((BulletRequestHeader) this.header.clone());
        return bulletRequest;
    }
}
