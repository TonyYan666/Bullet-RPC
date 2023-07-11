package com.smileframework.bullet.transport.server.netty.codec;

import com.smileframework.bullet.transport.common.exception.transport.server.BulletServerCoderException;
import com.smileframework.bullet.transport.common.protocol.TransportResponseProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 协议编码器
 */
public class TransportResponseEncoder extends MessageToMessageEncoder<TransportResponseProto.TransportResponse> {


    @Override
    protected void encode(ChannelHandlerContext ctx, TransportResponseProto.TransportResponse response, List<Object> out) throws Exception {
        try {
            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeBytes(response.toByteArray());
            out.add(byteBuf);
        } catch (Exception e) {
            ctx.fireExceptionCaught(new BulletServerCoderException(e));
        }
    }
}
