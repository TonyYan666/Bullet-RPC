package com.smileframework.bullet.transport.client.connection.future;

import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.lang.reflect.Type;

public class DefaultResponseTypesProvider implements ResponseTypesProvider {

    private Type[] types;

    public DefaultResponseTypesProvider(Type[] types) {
        this.types = types;
    }

    @Override
    public Type[] responseTypes(BulletResponse<byte[]> response) {
        return this.types;
    }
}
