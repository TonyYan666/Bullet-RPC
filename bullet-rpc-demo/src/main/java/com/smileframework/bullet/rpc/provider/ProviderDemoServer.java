package com.smileframework.bullet.rpc.provider;

import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;

import java.util.concurrent.ExecutionException;

public class ProviderDemoServer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        BulletTransportServerConfig serverConfig = new BulletTransportServerConfig();
        BulletProviderContext bulletProviderContext = new BulletProviderContext();
        bulletProviderContext.init(serverConfig);
        bulletProviderContext.setWorkExecutor(30, 100, 3, 100);
        bulletProviderContext.providerRegister(new DemoServiceProvider());
        bulletProviderContext.doOpen();
        bulletProviderContext.getTransportServer().getChannel().closeFuture().get();
    }

}
