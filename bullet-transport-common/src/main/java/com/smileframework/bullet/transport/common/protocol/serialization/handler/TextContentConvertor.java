package com.smileframework.bullet.transport.common.protocol.serialization.handler;

import cn.hutool.core.util.ArrayUtil;
import com.smileframework.bullet.transport.common.exception.rpc.serialization.BulletContentConvertException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class TextContentConvertor extends AbstractContentConvertor {

    @Override
    public int supportContentType() {
        return BulletContentType.TEXT;
    }

    @Override
    <T> T convertPayloadToObject(BulletRequest<byte[]> request, Type... types) {
        String result = null;
        if (ArrayUtil.isNotEmpty(request.getPayload())) {
            result = new String(request.getPayload(), StandardCharsets.UTF_8);
        }
        return (T) result;
    }

    @Override
    <T> byte[] convertPayloadToBytes(BulletRequest<T> request) {
        if (request.getPayload() == null) {
            return null;
        }
        if (request.getPayload() instanceof String) {
            return ((String) request.getPayload()).getBytes(StandardCharsets.UTF_8);
        }
        throw new BulletContentConvertException("[Bullet-Content-Convert] Text convertor could not convert object.");
    }

    @Override
    <T> byte[] convertResponseToBytes(BulletResponse<T> response) {
        if (response.getResponse() == null) {
            return null;
        }
        if (response.getResponse() instanceof String) {
            return ((String) response.getResponse()).getBytes(StandardCharsets.UTF_8);
        }
        throw new BulletContentConvertException("[Bullet-Content-Convert] Text convertor could not convert object.");
    }

    @Override
    <T> T convertResponseToObject(BulletResponse<byte[]> response, Type... types) {
        String result = null;
        if (ArrayUtil.isNotEmpty(response.getResponse())) {
            result = new String(response.getResponse(), StandardCharsets.UTF_8);
        }
        return (T) result;
    }


}
