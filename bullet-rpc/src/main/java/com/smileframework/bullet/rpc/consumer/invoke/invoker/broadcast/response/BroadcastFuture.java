package com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class BroadcastFuture<T> implements Future<List<T>> {

    private List<CompletableFuture> completableFutures = new ArrayList<>();

    private CompletableFuture allOfFuture;

    public BroadcastFuture(Map<String, CompletableFuture<T>> futureMap) {
        for (CompletableFuture<T> value : futureMap.values()) {
            completableFutures.add(value);
        }
        this.allOfFuture = CompletableFuture.allOf(this.completableFutures.toArray(new CompletableFuture[this.completableFutures.size()]));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.allOfFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.allOfFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.allOfFuture.isDone();
    }

    @Override
    public List<T> get() throws InterruptedException, ExecutionException {
        List<T> resultList = new ArrayList<>();
        for (CompletableFuture completableFuture : this.completableFutures) {
            resultList.add((T) completableFuture.get());
        }
        return resultList;
    }

    @Override
    public List<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.allOfFuture.get(timeout, unit);
        return this.get();
    }
}
