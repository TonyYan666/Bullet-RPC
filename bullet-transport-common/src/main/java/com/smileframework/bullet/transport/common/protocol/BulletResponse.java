package com.smileframework.bullet.transport.common.protocol;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.transport.common.exception.transport.server.OperationTypeUnsupportedException;
import com.smileframework.bullet.transport.common.protocol.code.BulletResponseCode;
import com.smileframework.bullet.transport.common.protocol.header.BulletResponseHeader;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 协议响应
 */
@Getter
@Setter
public class BulletResponse<T> implements Serializable {

    public static final <T> BulletResponse<T> createErrorResponse(BulletRequest request, String errorCode, Throwable e, String reason) {
        BulletResponseHeader header = BulletResponseHeader.createByRequestHeader(request.getHeader());
        header.setCode(errorCode);
        if (e != null) {
            header.setErrorReason(e.getMessage());
            header.setExceptionClz(e.getClass().getName());
        }
        if (StrUtil.isNotEmpty(reason)) {
            header.setErrorReason(reason);
        }
        BulletResponse response = new BulletResponse();
        response.setHeader(header);
        return response;
    }

    public static final <T> BulletResponse<T> createErrorResponse(BulletRequest request, String errorCode, Class<? extends Throwable> exceptionClz, String reason) {
        BulletResponseHeader header = BulletResponseHeader.createByRequestHeader(request.getHeader());
        header.setCode(errorCode);
        if (exceptionClz != null) {
            header.setExceptionClz(exceptionClz.getName());
        }
        if (StrUtil.isNotEmpty(reason)) {
            header.setErrorReason(reason);
        }
        BulletResponse response = new BulletResponse();
        response.setHeader(header);
        return response;
    }

    public static final <T> BulletResponse<T> createSuccessResponse(BulletRequest request, T responseData) {
        BulletResponseHeader header = BulletResponseHeader.createByRequestHeader(request.getHeader());
        header.setCode(BulletResponseCode.SUCCESS);
        BulletResponse response = new BulletResponse();
        response.setHeader(header);
        response.setResponse(responseData);
        return response;
    }

    public static <T> BulletResponse<T> createUnsupportedOperationResponse(BulletRequest request) {
        BulletResponse bulletTransportResponse = new BulletResponse();
        BulletResponseHeader header = new BulletResponseHeader();
        header.setCode(BulletResponseCode.OPERATION_TYPE_UNSUPPORTED);
        header.setOperationType(request.getHeader().getOperationType());
        header.setRequestId(request.getHeader().getRequestId());
        header.setErrorReason("Operation type unsupported.");
        header.setExceptionClz(OperationTypeUnsupportedException.class.getName());
        header.setActionURL(request.getHeader().getActionURL());
        bulletTransportResponse.setHeader(header);
        return bulletTransportResponse;
    }

    public static <T> BulletResponse<T> createRequestHandleErrorResponse(BulletRequest request, Throwable e) {
        BulletResponse bulletTransportResponse = new BulletResponse();
        BulletResponseHeader header = new BulletResponseHeader();
        header.setCode(BulletResponseCode.INVOKE_ERROR);
        header.setOperationType(request.getHeader().getOperationType());
        header.setRequestId(request.getHeader().getRequestId());
        header.setErrorReason("Handle request error.");
        if (e != null) {
            header.setExceptionClz(e.getClass().getName());
        }
        header.setActionURL(request.getHeader().getActionURL());
        bulletTransportResponse.setHeader(header);
        return bulletTransportResponse;
    }

    /**
     * 协议头
     */
    private BulletResponseHeader header;

    /**
     * 返回数据
     */
    private T response;

    @Override
    public String toString() {
        return "BulletResponse{" +
                "header=" + header +
                ", response=" + response +
                '}';
    }
}
