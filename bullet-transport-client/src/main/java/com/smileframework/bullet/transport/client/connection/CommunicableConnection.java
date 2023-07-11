package com.smileframework.bullet.transport.client.connection;

import cn.hutool.core.collection.CollectionUtil;
import com.smileframework.bullet.transport.client.connection.future.BulletResponseFuture;
import com.smileframework.bullet.transport.client.connection.future.DefaultResponseTypesProvider;
import com.smileframework.bullet.transport.client.connection.future.ResponseTypesProvider;
import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import com.smileframework.bullet.transport.client.error.ResponseErrorHandler;
import com.smileframework.bullet.transport.common.exception.connection.CheckServerConnectionException;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientCoderException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.common.protocol.header.BulletRequestHeader;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class CommunicableConnection extends AbstractConnection {

    /**
     * 内容转换器
     */
    private ContentConvertManager contentConvertManager;

    /**
     * 回调执行器
     */
    private Executor workerExecutor;

    /**
     * 返回异常处理器
     */
    private ResponseErrorHandler errorHandle;

    /**
     * 请求future
     */
    private Map<String, BulletResponseFuture> responseFutureMap = new ConcurrentHashMap<>();

    public CommunicableConnection(ServerConnectionProperties properties) {
        super(properties);
    }

    protected void setContentConvertManager(ContentConvertManager contentConvertManager) {
        this.contentConvertManager = contentConvertManager;
    }

    protected void setWorkerExecutor(Executor workerExecutor) {
        this.workerExecutor = workerExecutor;
    }

    protected void setErrorHandle(ResponseErrorHandler errorHandle) {
        this.errorHandle = errorHandle;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BulletResponse<byte[]> bulletResponse) throws Exception {
        BulletResponseFuture future = this.responseFutureMap.get(bulletResponse.getHeader().getRequestId());
        if (future != null) {
            this.responseFutureMap.remove(bulletResponse.getHeader().getRequestId());
            try {
                Type[] responseTypes = future.getResponseTypesProvider().responseTypes(bulletResponse);
                BulletResponse response = this.contentConvertManager.responseBytesToObject(bulletResponse, responseTypes);
                if (this.workerExecutor != null) {
                    this.workerExecutor.execute(() -> future.setSuccess(response));
                } else {
                    future.setSuccess(response);
                }
            } catch (Exception e) {
                future.setFailure(e);
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BulletClientCoderException) {
            BulletClientCoderException clientCoderException = (BulletClientCoderException) cause;
            BulletResponseFuture future = this.responseFutureMap.get(clientCoderException.getRequestId());
            if (future != null) {
                this.responseFutureMap.remove(clientCoderException.getRequestId());
                future.setFailure(cause);
                return;
            }
            log.error("Bullet Client communicable connection clientCoderException caught, but bullet response future could not be found.  ", cause);

        }
        log.error("Bullet Client communicable connection exception caught log.  ", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 异步发送请求
     *
     * @param request
     * @param responseTypes
     * @param <PAYLOAD>
     * @param <RESPONSE>
     * @return
     */
    public <PAYLOAD, RESPONSE> BulletResponseFuture<PAYLOAD, RESPONSE> sendRequest(BulletRequest<PAYLOAD> request, Type... responseTypes) {
        return this.sendRequest(request, null, new DefaultResponseTypesProvider(responseTypes));
    }

    /**
     * 异步发送请求
     *
     * @param request
     * @param typesProvider
     * @param <PAYLOAD>
     * @param <RESPONSE>
     * @return
     */
    public <PAYLOAD, RESPONSE> BulletResponseFuture<PAYLOAD, RESPONSE> sendRequest(BulletRequest<PAYLOAD> request, ResponseTypesProvider typesProvider) {
        return this.sendRequest(request, null, typesProvider);
    }

    /**
     * 异步发送请求
     */
    public <PAYLOAD, RESPONSE> BulletResponseFuture<PAYLOAD, RESPONSE> sendRequest(BulletRequest<PAYLOAD> request, List<GenericFutureListener<Future<? super BulletResponse<RESPONSE>>>> listeners, Type... responseTypes) {
        return this.sendRequest(request, listeners, new DefaultResponseTypesProvider(responseTypes));
    }

    /**
     * 异步发送请求
     */
    public <PAYLOAD, RESPONSE> BulletResponseFuture<PAYLOAD, RESPONSE> sendRequest(BulletRequest<PAYLOAD> request, List<GenericFutureListener<Future<? super BulletResponse<RESPONSE>>>> listeners, ResponseTypesProvider typesProvider) {
        if (request.getHeader().getOperationType() == BulletRequestHeader.OPERATION_TYPE_ACTION || request.getHeader().getOperationType() == BulletRequestHeader.OPERATION_TYPE_NOTIFICATION) {
            this.updateLastCommunicatedTime();
        }
        BulletRequest<byte[]> bulletBytesRequest = this.contentConvertManager.payloadObjectToBytes(request);
        BulletResponseFuture<PAYLOAD, RESPONSE> responseFuture = new BulletResponseFuture<>(request, typesProvider);
        if (CollectionUtil.isNotEmpty(listeners)) {
            for (GenericFutureListener<Future<? super BulletResponse<RESPONSE>>> listener : listeners) {
                responseFuture.addListener(listener);
            }
        }
        if (request.getHeader().getOperationType() != BulletRequestHeader.OPERATION_TYPE_NOTIFICATION) {
            this.responseFutureMap.put(responseFuture.getRequestId(), responseFuture);
        }
        this.nettyClient.writeAndFlush(bulletBytesRequest);
        if (request.getHeader().getOperationType() == BulletRequestHeader.OPERATION_TYPE_NOTIFICATION) {
            responseFuture.setSuccess(null);
        }
        return responseFuture;
    }


    public <PAYLOAD, RESPONSE> Mono<BulletResponse<RESPONSE>> sendReactiveRequest(BulletRequest<PAYLOAD> request, ResponseTypesProvider typesProvider) {
        Mono<BulletResponse<RESPONSE>> mono = Mono.create(sink -> {
            List<GenericFutureListener<Future<? super BulletResponse<RESPONSE>>>> listeners = new ArrayList<>();
            listeners.add(future -> {
                try {
                    BulletResponse<RESPONSE> response = (BulletResponse<RESPONSE>) future.get();
                    sink.success(response);
                } catch (Exception e) {
                    sink.error(e);
                }
            });
            this.sendRequest(request, listeners, typesProvider);
        });
        return mono.publishOn(Schedulers.boundedElastic());
    }


    /**
     * 同步发送请求
     */
    public <PAYLOAD, RESPONSE> BulletResponse<RESPONSE> syncSendRequest(BulletRequest<PAYLOAD> request, Duration timeout, Type... responseTypes) throws ExecutionException, InterruptedException, TimeoutException {
        BulletResponseFuture<PAYLOAD, RESPONSE> responseFuture = this.sendRequest(request, responseTypes);
        return responseFuture.get(timeout.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 同步发送请求
     */
    public <PAYLOAD, RESPONSE> BulletResponse<RESPONSE> syncSendRequest(BulletRequest<PAYLOAD> request, Duration timeout, ResponseTypesProvider typesProvider) throws ExecutionException, InterruptedException, TimeoutException {
        BulletResponseFuture<PAYLOAD, RESPONSE> responseFuture = this.sendRequest(request, typesProvider);
        return responseFuture.get(timeout.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 同步发送并直接获得返回对象，忽略头部以及其他信息
     * 如response头部出现错误码，则通过错误处理器，根据不同的错误代码抛出异常
     *
     * @param request
     * @param timeout
     * @param responseTypes
     * @param <PAYLOAD>
     * @param <RESPONSE>
     * @return
     */
    public <PAYLOAD, RESPONSE> RESPONSE sendAndReturnObject(BulletRequest<PAYLOAD> request, Duration timeout, Type... responseTypes) throws ExecutionException, InterruptedException, TimeoutException {
        BulletResponse<RESPONSE> response = this.syncSendRequest(request, timeout, responseTypes);
        this.errorHandle.errorHandle(response);
        return response.getResponse();
    }

    @Override
    protected void checkBeforeConnect() {
        super.checkBeforeConnect();
        if (this.errorHandle == null) {
            throw new CheckServerConnectionException("Response error handler didn't set.");
        }
        if (this.contentConvertManager == null) {
            throw new CheckServerConnectionException("Content convert manager didn't set.");
        }
    }
}