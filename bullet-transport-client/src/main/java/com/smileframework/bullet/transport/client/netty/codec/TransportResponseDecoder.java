package com.smileframework.bullet.transport.client.netty.codec;

import com.smileframework.bullet.transport.common.protocol.TransportResponseProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * bullet 协议请求解码
 */
public class TransportResponseDecoder extends MessageToMessageDecoder<ByteBuf> {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            byte[] arrays = new byte[msg.readableBytes()];
            msg.readBytes(arrays);
            TransportResponseProto.TransportResponse transportResponse = TransportResponseProto.TransportResponse.parseFrom(arrays);
            out.add(transportResponse);
        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
        }
    }

}
