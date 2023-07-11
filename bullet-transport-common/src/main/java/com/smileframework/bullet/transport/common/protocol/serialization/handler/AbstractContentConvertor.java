package com.smileframework.bullet.transport.common.protocol.serialization.handler;

import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.lang.reflect.Type;

/**
 * 抽象内容转换器
 * 根据实际的转换方法，并包装新的response 和 request 返回
 */
public abstract class AbstractContentConvertor implements ContentConvertor {

    @Override
    public <T> BulletRequest<T> payloadBytesToObject(BulletRequest<byte[]> request, Type... types) {
        BulletRequest<T> result = new BulletRequest<>();
        result.setHeader(request.getHeader());
        if (request.getPayload() != null && request.getPayload().length > 0) {
            result.setPayload(this.convertPayloadToObject(request, types));
        }
        return result;
    }

    @Override
    public <T> BulletRequest<byte[]> payloadObjectToBytes(BulletRequest<T> request) {
        BulletRequest<byte[]> result = new BulletRequest<>();
        if (request.getPayload() != null) {
            result.setPayload(this.convertPayloadToBytes(request));
        }
        result.setHeader(request.getHeader());
        return result;
    }

    @Override
    public <T> BulletResponse<byte[]> responseObjectToBytes(BulletResponse<T> response) {
        BulletResponse<byte[]> result = new BulletResponse<>();
        if (response.getResponse() != null) {
            result.setResponse(this.convertResponseToBytes(response));
        }
        result.setHeader(response.getHeader());
        return result;
    }

    @Override
    public <T> BulletResponse<T> responseBytesToObject(BulletResponse<byte[]> response, Type... types) {
        BulletResponse<T> result = new BulletResponse<>();
        if (response.getResponse() != null && response.getResponse().length > 0) {
            result.setResponse(this.convertResponseToObject(response, types));
        }
        result.setHeader(response.getHeader());
        return result;
    }

    /**
     * 转换request 到 object
     */
    abstract <T> T convertPayloadToObject(BulletRequest<byte[]> request, Type... types);

    /**
     * 转换 request 到 对象
     */
    abstract <T> byte[] convertPayloadToBytes(BulletRequest<T> request);

    /**
     * 转换 response 到 byte[]
     *
     * @param response
     * @param <T>
     * @return
     */
    abstract <T> byte[] convertResponseToBytes(BulletResponse<T> response);

    /**
     * 转换 response 到 对象
     *
     * @param response
     * @param types
     * @param <T>
     * @return
     */
    abstract <T> T convertResponseToObject(BulletResponse<byte[]> response, Type... types);

};
