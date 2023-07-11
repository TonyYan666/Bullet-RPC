package com.smileframework.bullet.rpc;

import com.smileframework.bullet.rpc.provider.BulletProviderContext;
import com.smileframework.bullet.rpc.provider.BulletTestProvider;
import com.smileframework.bullet.transport.common.protocol.serialization.ContentConvertManager;
import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;
import org.junit.Test;

public class BulletRpcProviderServerTest {

    @Test
    public void test() throws InterruptedException {
        //创建一个服务配置
        BulletTransportServerConfig config = new BulletTransportServerConfig();
        //监听端口3204 默认 2186
        config.setPort(3204);
        //内容转换管理器
        ContentConvertManager contentConvertManager = new ContentConvertManager();
        //创建一个BulletProviderContext对象
        BulletProviderContext providerContext = new BulletProviderContext();
        //使用 BulletTransportServerConfig 和 ContentConvertManager 初始化 创建一个BulletProviderContext对象
        providerContext.init(config,contentConvertManager);
        //配置调用执行线程池
        providerContext.setWorkExecutor(30,50,10,1);
        //创建一个provider 并添加注册
        BulletTestProvider provider = new BulletTestProvider();
        providerContext.providerRegister(provider);
        //启动并监听服务
        providerContext.doOpen();
        providerContext.getTransportServer().getChannel().closeFuture().sync();
    }

}
