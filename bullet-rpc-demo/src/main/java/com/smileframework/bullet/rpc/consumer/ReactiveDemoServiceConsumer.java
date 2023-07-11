package com.smileframework.bullet.rpc.consumer;

import com.smileframework.bullet.rpc.consumer.definition.annotation.ServiceConsumer;
import com.smileframework.bullet.rpc.consumer.definition.annotation.ServiceConsumerMethod;
import com.smileframework.bullet.rpc.pojo.request.DemoRpcRequest;
import com.smileframework.bullet.rpc.pojo.response.DemoRpcResponse;
import reactor.core.publisher.Mono;

@ServiceConsumer(serverAddress = "bullet://localhost", providerPath = "/DemoServiceProvider")
public interface ReactiveDemoServiceConsumer {

    @ServiceConsumerMethod(preferResponseActualType = true, transportArgumentsTypes = true)
    Mono<DemoRpcResponse> request(DemoRpcRequest request, String additionalParameter, int number);

}