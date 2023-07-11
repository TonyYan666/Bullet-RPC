package com.smileframework.bullet.transport.client.netty.codec;

import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientCoderException;
import com.smileframework.bullet.transport.common.protocol.TransportRequestProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * bullet 协议 请求编码
 */
public class TransportRequestEncoder extends MessageToMessageEncoder<TransportRequestProto.TransportRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransportRequestProto.TransportRequest request, List<Object> out) throws Exception {
        try {
            byte[] data = request.toByteArray();
            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeBytes(data);
            out.add(byteBuf);
        } catch (Exception e) {
            ctx.fireExceptionCaught(new BulletClientCoderException(request.getHeader().getRequestId(), e));
        }
    }
}
