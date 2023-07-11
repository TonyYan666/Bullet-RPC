package com.smileframework.bullet.transport.client.netty.codec;

import cn.hutool.core.util.StrUtil;
import com.google.protobuf.ByteString;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientCoderException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.TransportRequestProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * bullet 协议 请求编码
 */
public class BulletRequestProtocolEncoder extends MessageToMessageEncoder<BulletRequest<byte[]>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BulletRequest<byte[]> request, List<Object> out) throws Exception {
        try {
            TransportRequestProto.TransportRequestHeader requestHeader = TransportRequestProto.TransportRequestHeader.newBuilder()
                    .setRequestId(request.getHeader().getRequestId())
                    .setOperationType(request.getHeader().getOperationType())
                    .setContentType(request.getHeader().getContentType())
                    .setAuthorization(StrUtil.nullToEmpty(request.getHeader().getAuthorization()))
                    .setActionURL(StrUtil.nullToEmpty(request.getHeader().getActionURL()))
                    .putAllMetadata(request.getHeader().getMetadata())
                    .setConvertDesc(StrUtil.nullToEmpty(request.getHeader().getConvertDesc()))
                    .build();
            TransportRequestProto.TransportRequest.Builder transportRequestBuilder = TransportRequestProto.TransportRequest.newBuilder();
            transportRequestBuilder.setHeader(requestHeader);
            if (request.getPayload() != null) {
                transportRequestBuilder.setPayload(ByteString.copyFrom(request.getPayload()));
            }
            out.add(transportRequestBuilder.build());
        } catch (Exception e) {
            ctx.fireExceptionCaught(new BulletClientCoderException(request.getHeader().getRequestId(), e));
        }
    }
}
