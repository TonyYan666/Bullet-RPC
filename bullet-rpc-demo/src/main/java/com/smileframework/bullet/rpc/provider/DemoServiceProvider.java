package com.smileframework.bullet.rpc.provider;


import com.smileframework.bullet.rpc.pojo.request.DemoRpcRequest;
import com.smileframework.bullet.rpc.pojo.response.DemoRpcResponse;
import com.smileframework.bullet.rpc.pojo.response.SubRpcResponse;
import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProvider;
import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProviderMethod;

@ServiceProvider()
public class DemoServiceProvider {

    @ServiceProviderMethod(transportResponseType = true, preferArgumentsActualTypes = true)
    public DemoRpcResponse request(DemoRpcRequest request, String additionalParameter, int number) throws InterruptedException {
        SubRpcResponse response = new SubRpcResponse();
        response.setResult(request.getDemoContent() + "[" + additionalParameter + "]");
        response.setResultCode(number + request.getIndex());
        response.setOther("AAA");
        response.setSubIndex(number * number);
        Thread.sleep(2000);
        return response;
    }
}
