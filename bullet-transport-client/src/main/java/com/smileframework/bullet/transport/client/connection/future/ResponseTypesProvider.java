package com.smileframework.bullet.transport.client.connection.future;

import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.lang.reflect.Type;

public interface ResponseTypesProvider {

    Type[] responseTypes(BulletResponse<byte[]> response);

}
