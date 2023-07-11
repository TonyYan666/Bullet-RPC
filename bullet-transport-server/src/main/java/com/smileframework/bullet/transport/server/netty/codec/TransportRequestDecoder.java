package com.smileframework.bullet.transport.server.netty.codec;

import com.smileframework.bullet.transport.common.exception.transport.server.BulletServerCoderException;
import com.smileframework.bullet.transport.common.protocol.TransportRequestProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class TransportRequestDecoder extends MessageToMessageDecoder<ByteBuf> {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        try {
            byte[] arrays = new byte[msg.readableBytes()];
            msg.readBytes(arrays);
            TransportRequestProto.TransportRequest request = TransportRequestProto.TransportRequest.parseFrom(arrays);
            list.add(request);
        } catch (Exception e) {
            channelHandlerContext.fireExceptionCaught(new BulletServerCoderException(e));
        }
    }
}
