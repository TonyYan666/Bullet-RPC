package com.smileframework.bullet.rpc.provider;

import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProvider;

@ServiceProvider
public class BulletTestProvider {

    public String test(){
        return "Hello world";
    }

}
