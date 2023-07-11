package com.smileframework.bullet.transport.common.protocol.header;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应协议头
 */
@Getter
@Setter
public class BulletResponseHeader implements Serializable {

    public static final int OPERATION_TYPE_HEARTBEAT = 0;

    public static final int OPERATION_TYPE_HANDSHAKE = 1;

    public static final int OPERATION_TYPE_ACTION = 2;

    public static BulletResponseHeader createByRequestHeader(BulletRequestHeader requestHeader) {
        BulletResponseHeader responseHeader = new BulletResponseHeader();
        responseHeader.setActionURL(requestHeader.getActionURL());
        responseHeader.setRequestId(requestHeader.getRequestId());
        responseHeader.setOperationType(requestHeader.getOperationType());
        responseHeader.setContentType(requestHeader.getContentType());
        return responseHeader;
    }

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 行为类型 0-心跳检测 1-连接握手 2-RPC请求
     */
    private int operationType;

    /**
     * 内容类型 0-JSON
     */
    private int contentType = BulletContentType.JSON;

    /**
     * 请求URL
     */
    private String actionURL;

    /**
     * 返回编码
     */
    private String code;

    /**
     * 失败原因
     */
    private String errorReason;

    /**
     * 异常类
     */
    private String exceptionClz;

    /**
     * 转换描述【用于解决RPC的传参多态性问题】
     * 参数可以指定基类或者父类，通过Bullet RPC 框架在返回对象时获得最终的实际类型，绑定到该头部当中；
     */
    private String convertDesc;

    /**
     * 附加元数据
     */
    private Map<String, String> metadata = new HashMap<>();


    @Override
    public String toString() {
        return "BulletResponseHeader{" +
                "actionURL='" + actionURL + '\'' +
                ", code='" + code + '\'' +
                ", errorReason='" + errorReason + '\'' +
                ", exceptionClz='" + exceptionClz + '\'' +
                '}';
    }
}
