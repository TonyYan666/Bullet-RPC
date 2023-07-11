package com.smileframework.bullet.rpc.consumer.invoke.filter;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.transport.client.connection.ServerConnection;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;

public interface ConsumerPreResponseFilter {

    int filterOrder();

    <T> boolean shouldFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<T> response, Throwable throwable, ServerConnection connection);

    <T> void doFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<T> response, ServerConnection connection, long costMs);

    <T> void doFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, Throwable throwable, ServerConnection connection, long costMs);

}
