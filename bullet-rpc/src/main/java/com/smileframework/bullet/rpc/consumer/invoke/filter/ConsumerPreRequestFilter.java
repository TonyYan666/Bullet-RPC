package com.smileframework.bullet.rpc.consumer.invoke.filter;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.transport.client.connection.ServerConnection;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;

public interface ConsumerPreRequestFilter {

    int filterOrder();

    boolean shouldFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, ServerConnection connection);

    <T> void doFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, ServerConnection connection);

}
