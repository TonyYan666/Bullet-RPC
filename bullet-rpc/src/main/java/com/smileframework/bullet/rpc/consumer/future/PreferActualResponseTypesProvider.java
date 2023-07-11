package com.smileframework.bullet.rpc.consumer.future;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.transport.client.connection.future.ResponseTypesProvider;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.lang.reflect.Type;

public class PreferActualResponseTypesProvider implements ResponseTypesProvider {

    private Type[] types;

    public PreferActualResponseTypesProvider(Type[] types) {
        this.types = types;
    }

    @Override
    public Type[] responseTypes(BulletResponse<byte[]> response) {
        if (this.types == null || types.length == 0) {
            return this.types;
        }
        if (StrUtil.isBlank(response.getHeader().getConvertDesc())) {
            return this.types;
        }
        String[] typeNames = response.getHeader().getConvertDesc().split(",");
        if (typeNames.length != types.length) {
            return this.types;
        }
        Type[] actualTypes = new Type[this.types.length];
        for (int i = 0; i < typeNames.length; i++) {
            String typeName = typeNames[i];
            if (StrUtil.isBlank(typeName)) {
                actualTypes[i] = this.types[i];
                continue;
            }
            try {
                actualTypes[i] = Class.forName(typeName);
            } catch (Exception e) {
                actualTypes[i] = this.types[i];
            }
        }
        return actualTypes;
    }
}
