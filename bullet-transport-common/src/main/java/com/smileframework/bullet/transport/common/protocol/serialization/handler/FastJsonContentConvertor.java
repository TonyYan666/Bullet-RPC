package com.smileframework.bullet.transport.common.protocol.serialization.handler;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class FastJsonContentConvertor extends AbstractContentConvertor {

    @Override
    public int supportContentType() {
        return BulletContentType.JSON;
    }

    @Override
    <T> T convertPayloadToObject(BulletRequest<byte[]> request, Type... types) {
        byte[] data = request.getPayload();
        if (ArrayUtil.isEmpty(data)) {
            return null;
        }
        if (ArrayUtil.isEmpty(types)) {
            return null;
        }
        String jsonString = new String(data, StandardCharsets.UTF_8);
        Object json = JSON.parse(jsonString);
        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            Object[] resultArray = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                Type type = types[i];
                resultArray[i] = jsonArray.getObject(i, type);
            }
            return (T) resultArray;
        }
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            Type type = types[0];
            return jsonObject.toJavaObject(type);
        }
        return (T) JSON.parseObject(data, types[0]);
    }

    @Override
    <T> byte[] convertPayloadToBytes(BulletRequest<T> request) {
        return JSON.toJSONBytes(request.getPayload());
    }

    @Override
    <T> byte[] convertResponseToBytes(BulletResponse<T> response) {
        return JSON.toJSONBytes(response.getResponse());
    }

    @Override
    <T> T convertResponseToObject(BulletResponse<byte[]> response, Type... types) {
        byte[] data = response.getResponse();
        if (ArrayUtil.isEmpty(data)) {
            return null;
        }
        if (ArrayUtil.isEmpty(types)) {
            return null;
        }
        String jsonString = new String(data, StandardCharsets.UTF_8);
        Type type = types[0];
        return JSON.parseObject(jsonString, type);
    }


}