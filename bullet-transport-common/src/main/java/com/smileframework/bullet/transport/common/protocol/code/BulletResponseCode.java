package com.smileframework.bullet.transport.common.protocol.code;

/**
 * Bullet 协议响应编码常量
 */
public class BulletResponseCode {

    /**
     * 成功
     */
    public static final String SUCCESS = "200";

    /**
     * 传输通讯认证失败
     */
    public static final String TRANSPORT_AUTHENTICATION_FAIL = "401";

    /**
     * 提供者不存在
     */
    public static final String PROVIDER_NOT_FOUND = "404";

    /**
     * 执行异常
     */
    public static final String INVOKE_ERROR = "500";

    /**
     * 执行权限验证不通过
     */
    public static final String EXECUTION_AUTHENTICATION_FAIL = "403";

    /**
     * 协议操作类型不支持
     */
    public static final String OPERATION_TYPE_UNSUPPORTED = "501";

    /**
     * 服务提供者执行器 繁忙
     */
    public static final String EXECUTOR_IS_BUSY = "503";

    /**
     * 内容解析失败
     */
    public static final String CONTENT_TYPE_CONVERT_FAILURE = "415";

}
