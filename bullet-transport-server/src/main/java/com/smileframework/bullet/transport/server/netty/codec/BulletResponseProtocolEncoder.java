package com.smileframework.bullet.transport.server.netty.codec;

import cn.hutool.core.util.StrUtil;
import com.google.protobuf.ByteString;
import com.smileframework.bullet.transport.common.exception.transport.server.BulletServerCoderException;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.TransportResponseProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 协议编码器
 */
public class BulletResponseProtocolEncoder extends MessageToMessageEncoder<BulletResponse<byte[]>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BulletResponse<byte[]> response, List<Object> out) throws Exception {
        try {
            TransportResponseProto.TransportResponseHeader responseHeader = TransportResponseProto.TransportResponseHeader.newBuilder()
                    .setRequestId(response.getHeader().getRequestId())
                    .setOperationType(response.getHeader().getOperationType())
                    .setContentType(response.getHeader().getContentType())
                    .setActionURL(StrUtil.nullToEmpty(response.getHeader().getActionURL()))
                    .setCode(StrUtil.nullToEmpty(response.getHeader().getCode()))
                    .setErrorReason(StrUtil.nullToEmpty(response.getHeader().getErrorReason()))
                    .setExceptionClz(StrUtil.nullToEmpty(response.getHeader().getExceptionClz()))
                    .putAllMetadata(response.getHeader().getMetadata())
                    .setConvertDesc(StrUtil.nullToEmpty(response.getHeader().getConvertDesc()))
                    .build();
            TransportResponseProto.TransportResponse.Builder transportResponseBuilder = TransportResponseProto.TransportResponse.newBuilder();
            if (response.getResponse() != null) {
                transportResponseBuilder.setResponse(ByteString.copyFrom(response.getResponse()));
            }
            transportResponseBuilder.setHeader(responseHeader).build();
            out.add(transportResponseBuilder.build());
        } catch (Exception e) {
            ctx.fireExceptionCaught(new BulletServerCoderException(e));
        }
    }
}
