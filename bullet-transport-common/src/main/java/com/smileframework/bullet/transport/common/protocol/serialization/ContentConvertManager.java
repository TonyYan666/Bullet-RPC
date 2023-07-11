package com.smileframework.bullet.transport.common.protocol.serialization;

import com.smileframework.bullet.transport.common.exception.rpc.serialization.BulletContentConvertException;
import com.smileframework.bullet.transport.common.exception.rpc.serialization.ContentTypeNotSupportedException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.serialization.handler.ContentConvertor;
import com.smileframework.bullet.transport.common.protocol.serialization.handler.FastJsonContentConvertor;
import com.smileframework.bullet.transport.common.protocol.serialization.handler.TextContentConvertor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ContentConvertManager {

    public Map<Integer, ContentConvertor> contentConvertorMap = new HashMap<>();

    public ContentConvertManager() {
        FastJsonContentConvertor fastJsonContentConvertor = new FastJsonContentConvertor();
        TextContentConvertor textContentConvertor = new TextContentConvertor();
        this.contentConvertorMap.put(fastJsonContentConvertor.supportContentType(), fastJsonContentConvertor);
        this.contentConvertorMap.put(textContentConvertor.supportContentType(), textContentConvertor);
    }

    public void replaceContentConvertors(ContentConvertor... convertors) {
        for (ContentConvertor convertor : convertors) {
            this.contentConvertorMap.put(convertor.supportContentType(), convertor);
        }
    }

    public ContentConvertor getContentConvertor(int contentType) {
        ContentConvertor contentConvertor = this.contentConvertorMap.get(contentType);
        if (contentConvertor == null) {
            throw new ContentTypeNotSupportedException("[Bullet-Transport-Convert] content type [" + contentType + "] could not supported.");
        }
        return contentConvertor;
    }

    /**
     * 请求 byte[] 转为 对象 并返回新的request
     *
     * @param request
     * @param types
     * @param <T>
     * @return
     */
    public <T> BulletRequest<T> payloadBytesToObject(BulletRequest<byte[]> request, Type... types) {
        ContentConvertor contentConvertor = getContentConvertor(request.getHeader().getContentType());
        try {
            return contentConvertor.payloadBytesToObject(request, types);
        } catch (Exception e) {
            throw new BulletContentConvertException("Bullet request content convert error, " + e.getMessage(), e);
        }
    }

    /**
     * 请求 对象 转为 byte[] 并返回新的request
     *
     * @param request
     * @param <T>
     * @return
     */
    public <T> BulletRequest<byte[]> payloadObjectToBytes(BulletRequest<T> request) {
        ContentConvertor contentConvertor = getContentConvertor(request.getHeader().getContentType());
        try {
            return contentConvertor.payloadObjectToBytes(request);
        } catch (Exception e) {
            throw new BulletContentConvertException("Bullet request content convert error, " + e.getMessage(), e);
        }
    }

    /**
     * 将 响应 对象内容 转为 byte[] 并返回新的response
     *
     * @param response
     * @param <T>
     * @return
     */
    public <T> BulletResponse<byte[]> responseObjectToBytes(BulletResponse<T> response) {
        ContentConvertor contentConvertor = getContentConvertor(response.getHeader().getContentType());
        try {
            return contentConvertor.responseObjectToBytes(response);
        } catch (Exception e) {
            throw new BulletContentConvertException("Bullet response content convert error, " + e.getMessage(), e);
        }
    }

    /**
     * 将 响应 byte[] 转为 对象 并返回新的 response
     *
     * @param response
     * @param types
     * @param <T>
     * @return
     */
    public <T> BulletResponse<T> responseBytesToObject(BulletResponse<byte[]> response, Type... types) {
        ContentConvertor contentConvertor = getContentConvertor(response.getHeader().getContentType());
        try {
            return contentConvertor.responseBytesToObject(response, types);
        } catch (Exception e) {
            throw new BulletContentConvertException("Bullet response content convert error, " + e.getMessage(), e);
        }
    }


}
