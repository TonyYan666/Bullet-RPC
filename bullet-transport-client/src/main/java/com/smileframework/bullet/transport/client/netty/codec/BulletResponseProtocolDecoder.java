package com.smileframework.bullet.transport.client.netty.codec;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientCoderException;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.TransportResponseProto;
import com.smileframework.bullet.transport.common.protocol.header.BulletResponseHeader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.HashMap;
import java.util.List;

/**
 * bullet 协议请求解码
 */
public class BulletResponseProtocolDecoder extends MessageToMessageDecoder<TransportResponseProto.TransportResponse> {

    @Override
    protected void decode(ChannelHandlerContext ctx, TransportResponseProto.TransportResponse msg, List<Object> out) throws Exception {
        try {
            BulletResponse<byte[]> bulletResponse = new BulletResponse<>();
            BulletResponseHeader bulletResponseHeader = new BulletResponseHeader();
            bulletResponseHeader.setRequestId(msg.getHeader().getRequestId());
            bulletResponseHeader.setOperationType(msg.getHeader().getOperationType());
            bulletResponseHeader.setContentType(msg.getHeader().getContentType());
            bulletResponseHeader.setActionURL(msg.getHeader().getActionURL());
            bulletResponseHeader.setCode(msg.getHeader().getCode());
            bulletResponseHeader.setErrorReason(msg.getHeader().getErrorReason());
            bulletResponseHeader.setExceptionClz(msg.getHeader().getExceptionClz());
            bulletResponseHeader.setMetadata(new HashMap<>());
            bulletResponseHeader.getMetadata().putAll(msg.getHeader().getMetadataMap());
            bulletResponseHeader.setConvertDesc(msg.getHeader().getConvertDesc());
            bulletResponse.setResponse(msg.getResponse().toByteArray());
            bulletResponse.setHeader(bulletResponseHeader);
            out.add(bulletResponse);
        } catch (Exception e) {
            if (msg != null && msg.getHeader() != null && StrUtil.isNotBlank(msg.getHeader().getRequestId())) {
                ctx.fireExceptionCaught(new BulletClientCoderException(msg.getHeader().getRequestId()));
                return;
            }
            ctx.fireExceptionCaught(e);
        }
    }

}
