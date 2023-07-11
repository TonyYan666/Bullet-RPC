package com.smileframework.bullet.transport.common.protocol.header;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求协议头
 */
@Getter
@Setter
public class BulletRequestHeader implements Serializable, Cloneable {

    public static final int OPERATION_TYPE_HEARTBEAT = 0;

    public static final int OPERATION_TYPE_HANDSHAKE = 1;

    public static final int OPERATION_TYPE_ACTION = 2;

    public static final int OPERATION_TYPE_NOTIFICATION = 3;


    @JSONField(serialize = false)
    private URI serverAddress;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 行为类型 0-心跳检测 1-连接握手 2-RPC请求 3-通知请求
     */
    private int operationType;

    /**
     * 内容类型 0-JSON
     */
    private int contentType = BulletContentType.JSON;

    /**
     * 认证权限头
     */
    private String authorization;

    /**
     * 请求URL
     */
    private String actionURL;

    /**
     * 转换描述【用于解决RPC的传参多态性问题】
     * 参数可以指定基类或者父类，通过Bullet RPC 框架在发起请求是获得最终的实际类型，绑定到该头部当中；
     */
    private String convertDesc;

    /**
     * 附加元数据
     */
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public String toString() {
        return "BulletTransportRequestHeader{" +
                "requestId='" + requestId + '\'' +
                ", operationType=" + operationType +
                ", actionURL='" + actionURL + '\'' +
                '}';
    }

    @Override
    public Object clone() {
        BulletRequestHeader bulletRequestHeader = new BulletRequestHeader();
        bulletRequestHeader.setServerAddress(this.serverAddress);
        bulletRequestHeader.setRequestId(UUID.fastUUID().toString(true));
        bulletRequestHeader.setOperationType(this.operationType);
        bulletRequestHeader.setContentType(this.contentType);
        bulletRequestHeader.setAuthorization(this.authorization);
        bulletRequestHeader.setActionURL(this.actionURL);
        bulletRequestHeader.setConvertDesc(this.convertDesc);
        Map<String, String> meta = new HashMap<>();
        meta.putAll(this.metadata);
        bulletRequestHeader.setMetadata(meta);
        return bulletRequestHeader;
    }
}
