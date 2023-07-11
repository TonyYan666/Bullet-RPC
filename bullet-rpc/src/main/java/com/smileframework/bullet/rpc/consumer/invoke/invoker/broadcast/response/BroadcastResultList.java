package com.smileframework.bullet.rpc.consumer.invoke.invoker.broadcast.response;

import java.util.ArrayList;

public class BroadcastResultList<T> extends ArrayList {

    @Override
    public T get(int index) {
        Object result = super.get(index);
        if (result != null && result instanceof RuntimeException) {
            throw (RuntimeException) result;
        }
        return (T) result;
    }


}
