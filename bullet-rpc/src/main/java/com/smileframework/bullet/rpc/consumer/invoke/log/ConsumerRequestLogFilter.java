package com.smileframework.bullet.rpc.consumer.invoke.log;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.rpc.consumer.invoke.filter.ConsumerPreResponseFilter;
import com.smileframework.bullet.transport.client.connection.ServerConnection;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class ConsumerRequestLogFilter implements ConsumerPreResponseFilter {
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public <T> boolean shouldFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<T> response, Throwable throwable, ServerConnection connection) {
        return true;
    }

    @Override
    public <T> void doFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<T> response, ServerConnection connection, long costMs) {
        StringBuffer stringBuffer = new StringBuffer("[Bullet-Consumer] consumer  invoke cost[" + costMs + "ms] request-header[" + request.getHeader()
                + "] -> args[" + Arrays.toString(request.getPayload()) + "]");
        if (response != null) {
            stringBuffer.append(" response-header[" + response.getHeader() + "] -> " + response.getResponse());
        } else {
            stringBuffer.append(" no response .");
        }
        log.info(stringBuffer.toString());
    }

    @Override
    public <T> void doFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, Throwable throwable, ServerConnection connection, long costMs) {
        log.info("[Bullet-Consumer] consumer invoke cost[" + costMs + "ms] request-header[" + request.getHeader()
                + "] -> args[" + Arrays.toString(request.getPayload()) + "].", throwable);
    }
}
