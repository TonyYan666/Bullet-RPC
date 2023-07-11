package com.smileframework.bullet.transport.common.protocol.serialization.handler;

import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.lang.reflect.Type;

/**
 * Bullet 协议内容转换器
 */
public interface ContentConvertor {

    /**
     * 支持的内容类型
     *
     * @return
     */
    int supportContentType();

    /**
     * 请求 byte[] 转为 对象 并返回新的request
     *
     * @param request
     * @param types
     * @param <T>
     * @return
     */
    <T> BulletRequest<T> payloadBytesToObject(BulletRequest<byte[]> request, Type... types);

    /**
     * 请求 对象 转为 byte[] 并返回新的request
     *
     * @param request
     * @param <T>
     * @return
     */
    <T> BulletRequest<byte[]> payloadObjectToBytes(BulletRequest<T> request);

    /**
     * 将 响应 对象内容 转为 byte[] 并返回新的response
     *
     * @param response
     * @param <T>
     * @return
     */
    <T> BulletResponse<byte[]> responseObjectToBytes(BulletResponse<T> response);

    /**
     * 将 响应 byte[] 转为 对象 并返回新的 response
     *
     * @param response
     * @param types
     * @param <T>
     * @return
     */
    <T> BulletResponse<T> responseBytesToObject(BulletResponse<byte[]> response, Type... types);

}
