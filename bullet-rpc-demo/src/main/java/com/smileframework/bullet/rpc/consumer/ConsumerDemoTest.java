package com.smileframework.bullet.rpc.consumer;

import com.smileframework.bullet.rpc.pojo.request.SubRpcRequest;
import com.smileframework.bullet.rpc.pojo.response.DemoRpcResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerDemoTest {

    public static void main(String[] args) {
        BulletConsumerContext context = DefaultBulletConsumerContextBuilder.create().build();
        ReactiveDemoServiceConsumer consumer = context.getServiceConsumerProxy(ReactiveDemoServiceConsumer.class);
        SubRpcRequest request = new SubRpcRequest();
        request.setDemoContent("test");
        request.setIndex(1);
        request.setSubIndex(2);
        request.setOther("BBB");
        for (int i = 0; i < 100; i++) {
            final int number = i;
            consumer.request(request, "aaa", i).subscribe(resp -> {
                System.out.println("[" + Thread.currentThread().getName() + "](" + number + "):" + resp);
            }, e -> {
                System.err.println("error(" + number + "):" + e.getMessage());
            });
        }
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        context.shutdown();
    }


}
