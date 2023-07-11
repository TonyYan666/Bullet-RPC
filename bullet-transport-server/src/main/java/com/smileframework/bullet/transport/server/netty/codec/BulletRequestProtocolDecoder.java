package com.smileframework.bullet.transport.server.netty.codec;

import com.smileframework.bullet.transport.common.exception.transport.server.BulletServerCoderException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.TransportRequestProto;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.HashMap;
import java.util.List;

/**
 * 协议解码器
 */
public class BulletRequestProtocolDecoder extends MessageToMessageDecoder<TransportRequestProto.TransportRequest> {

    @Override
    protected void decode(ChannelHandlerContext ctx, TransportRequestProto.TransportRequest msg, List<Object> out) throws Exception {
        try {
            BulletRequest<byte[]> bulletTransportRequest = new BulletRequest<>();
            BulletRequestHeader requestHeader = new BulletRequestHeader();
            requestHeader.setRequestId(msg.getHeader().getRequestId());
            requestHeader.setOperationType(msg.getHeader().getOperationType());
            requestHeader.setContentType(msg.getHeader().getContentType());
            requestHeader.setAuthorization(msg.getHeader().getAuthorization());
            requestHeader.setActionURL(msg.getHeader().getActionURL());
            requestHeader.setMetadata(new HashMap<>());
            requestHeader.getMetadata().putAll(msg.getHeader().getMetadataMap());
            requestHeader.setConvertDesc(msg.getHeader().getConvertDesc());
            bulletTransportRequest.setHeader(requestHeader);
            bulletTransportRequest.setPayload(msg.getPayload().toByteArray());
            out.add(bulletTransportRequest);
        } catch (Exception e) {
            ctx.fireExceptionCaught(new BulletServerCoderException(e));
        }
    }


}
