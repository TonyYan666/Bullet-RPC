package com.smileframework.bullet.transport.common.netty.frame;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Bullet 数据报文帧解码
 */
public class BulletFrameDecoder extends LengthFieldBasedFrameDecoder {

    public BulletFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }

}
