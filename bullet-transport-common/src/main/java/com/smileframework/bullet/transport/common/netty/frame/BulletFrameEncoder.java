package com.smileframework.bullet.transport.common.netty.frame;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Bullet 数据报文帧编码
 */
public class BulletFrameEncoder extends LengthFieldPrepender {

    public BulletFrameEncoder() {
        super(4);
    }

}
