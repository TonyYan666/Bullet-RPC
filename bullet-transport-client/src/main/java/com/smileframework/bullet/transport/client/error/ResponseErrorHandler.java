package com.smileframework.bullet.transport.client.error;

import com.smileframework.bullet.transport.common.protocol.BulletResponse;

public interface ResponseErrorHandler {

    BulletResponse errorHandle(BulletResponse response);
}
