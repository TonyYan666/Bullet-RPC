package com.smileframework.bullet.rpc.consumer.invoke.filter;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerMethodDefinition;
import com.smileframework.bullet.transport.client.connection.ServerConnection;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;

import java.util.ArrayList;
import java.util.List;

public class ConsumerFilterManager {

    private List<ConsumerPreRequestFilter> preRequestFilters = new ArrayList<>();

    private List<ConsumerPreResponseFilter> preResponseFilters = new ArrayList<>();

    public void addPreRequestFilter(ConsumerPreRequestFilter requestFilter) {
        this.preRequestFilters.add(requestFilter);
        this.preRequestFilters.sort((a, b) -> Integer.valueOf(a.filterOrder()).compareTo(Integer.valueOf(b.filterOrder())));
    }

    public void addPreResponseFilter(ConsumerPreResponseFilter responseFilter) {
        this.preResponseFilters.add(responseFilter);
        this.preResponseFilters.sort((a, b) -> Integer.valueOf(a.filterOrder()).compareTo(Integer.valueOf(b.filterOrder())));
    }

    public void doPreRequestFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, ServerConnection connection) {
        if (this.preRequestFilters.isEmpty()) {
            return;
        }
        for (ConsumerPreRequestFilter preRequestFilter : this.preRequestFilters) {
            if (!preRequestFilter.shouldFilter(methodDefinition, request, connection)) {
                continue;
            }
            preRequestFilter.doFilter(methodDefinition, request, connection);
        }
    }

    public <T> void doPreResponseFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, BulletResponse<T> response, ServerConnection connection, long costMs) {
        if (this.preResponseFilters.isEmpty()) {
            return;
        }
        for (ConsumerPreResponseFilter preResponseFilter : this.preResponseFilters) {
            if (!preResponseFilter.shouldFilter(methodDefinition, request, response, null, connection)) {
                continue;
            }
            preResponseFilter.doFilter(methodDefinition, request, response, connection, costMs);
        }
    }

    public <T> void doPreResponseFilter(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, Throwable throwable, ServerConnection connection, long costMs) {
        if (this.preResponseFilters.isEmpty()) {
            return;
        }
        for (ConsumerPreResponseFilter preResponseFilter : this.preResponseFilters) {
            if (!preResponseFilter.shouldFilter(methodDefinition, request, null, throwable, connection)) {
                continue;
            }
            preResponseFilter.doFilter(methodDefinition, request, throwable, connection, costMs);
        }
    }

}
