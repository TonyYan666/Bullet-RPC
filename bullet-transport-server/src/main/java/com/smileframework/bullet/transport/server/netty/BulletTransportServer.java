package com.smileframework.bullet.transport.server.netty;

import com.smileframework.bullet.transport.common.netty.frame.BulletFrameDecoder;
import com.smileframework.bullet.transport.common.netty.frame.BulletFrameEncoder;
import com.smileframework.bullet.transport.common.netty.loop.NettyEventLoopFactory;
import com.smileframework.bullet.transport.server.connection.ClientConnectionManager;
import com.smileframework.bullet.transport.server.netty.codec.BulletRequestProtocolDecoder;
import com.smileframework.bullet.transport.server.netty.codec.BulletResponseProtocolEncoder;
import com.smileframework.bullet.transport.server.communication.ServerCommunicatedManager;
import com.smileframework.bullet.transport.server.config.BulletTransportServerConfig;
import com.smileframework.bullet.transport.server.netty.codec.TransportRequestDecoder;
import com.smileframework.bullet.transport.server.netty.codec.TransportResponseEncoder;
import com.smileframework.bullet.transport.server.netty.handler.ServerChannelEventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Bullet 通讯服务，主要监听指定端口
 */
@Slf4j
public class BulletTransportServer {

    public final static String NETTY_SERVER_BOSS_THREAD_FACTORY_NAME = "BulletNettyServerBoss";

    public final static String NETTY_SERVER_IO_WORKER_THREAD_FACTORY_NAME = "BulletNettyServerWorker";

    public final static String BULLET_SERVER_HANDLER_WORKER_THREAD_FACTORY_NAME = "BulletServerHandlerWorker";

    public static final int HEARTBEAT_TIME = 50;

    /**
     * Netty 的服务 Bootstrap
     */
    private ServerBootstrap bootstrap;

    /**
     * 服务端的channel
     */
    @Getter
    private Channel channel;

    /**
     * 接收连接请求的 LoopGroup
     */
    private EventLoopGroup bossGroup;

    /**
     * IO事件的 LoopGroup
     */
    private EventLoopGroup workerGroup;

    /**
     * 主要是用于业务处理的线程池（目前没有用，又RPC层统一进行做线程池管理）
     */
    private UnorderedThreadPoolEventExecutor bulletServerHandlerExecutor;

    /**
     * Bullet 服务通信的handler
     * 主要负责数据读取事件分发到 BulletServerCommunicatedManager
     * 将客户端连接事件分发到 ServerChannelManager
     */
    private ServerChannelEventHandler bulletServerHandler;

    /**
     * 服务端的连接管理器，主要完成客户端连接的注册与注销
     * 客户端连接的握手信息的处理
     * 客户端的连接关闭
     */
    private ClientConnectionManager serverChannelManager;

    /**
     * Bullet 服务器端的核心配置类
     */
    private BulletTransportServerConfig transportServerConfig;

    /**
     * 通讯交互管理器，主要负责请求数据的处理与分发，
     * 具体根据协议的请求行为分发到不同的BulletServerCommunicatedHandler实现。
     * 同时也会监听 原生 userEventTriggered 的回调事件方法，同样分发到不同的BulletServerCommunicatedHandler实现。
     */
    private ServerCommunicatedManager communicatedManager;

    /**
     * 是否已经绑定端口监听
     */
    private boolean isBound = false;

    /**
     * 构造方法 需要 连接管理器、Bullet 通讯层服务端配置、数据交互管理器
     *
     * @param serverChannelManager
     * @param transportServerConfig
     * @param communicatedManager
     */
    public BulletTransportServer(ClientConnectionManager serverChannelManager, BulletTransportServerConfig transportServerConfig, ServerCommunicatedManager communicatedManager) {
        this.serverChannelManager = serverChannelManager;
        this.transportServerConfig = transportServerConfig;
        this.communicatedManager = communicatedManager;
    }

    /**
     * 启动服务
     *
     * @return
     */
    public Channel doOpen() {
        this.bootstrap = new ServerBootstrap();
        this.bulletServerHandler = new ServerChannelEventHandler(this.serverChannelManager, this.communicatedManager);
        this.initServerExecutor();
        this.initServerBootstrap();
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(this.transportServerConfig.getPort()));
        log.info("Bullet transport server is ready. port : " + this.transportServerConfig.getPort());
        channelFuture.syncUninterruptibly();
        this.isBound = true;
        this.channel = channelFuture.channel();
        return this.channel;
    }

    /**
     * 关闭服务
     */
    public void doClose() {
        try {
            if (channel != null) {
                // unbind.
                channel.close().sync();
                this.isBound = false;

            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
        try {
            this.serverChannelManager.shutdown();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
        try {
            if (bootstrap != null) {
                long timeout = this.transportServerConfig.getShutdownTimeout().getSeconds();
                long quietPeriod = Math.min(2000L, timeout);
                Future<?> bossGroupShutdownFuture = bossGroup.shutdownGracefully(quietPeriod, timeout, TimeUnit.SECONDS);
                Future<?> workerGroupShutdownFuture = workerGroup.shutdownGracefully(quietPeriod, timeout, TimeUnit.SECONDS);
                bossGroupShutdownFuture.syncUninterruptibly();
                workerGroupShutdownFuture.syncUninterruptibly();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 初始化 Netty ServerBootstrap 的配置
     */
    private void initServerBootstrap() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new BulletFrameDecoder())
                                .addLast(new BulletFrameEncoder())
                                .addLast(new TransportResponseEncoder())
                                .addLast(new BulletResponseProtocolEncoder())
                                .addLast(new TransportRequestDecoder())
                                .addLast(new BulletRequestProtocolDecoder())
                                .addLast(new IdleStateHandler(HEARTBEAT_TIME,
                                        HEARTBEAT_TIME,
                                        HEARTBEAT_TIME, TimeUnit.SECONDS))
                                .addLast(BulletTransportServer.this.bulletServerHandlerExecutor, bulletServerHandler);
                    }
                });
    }


    /**
     * 初始化LoopGroup
     */
    private void initServerExecutor() {
        this.bossGroup = NettyEventLoopFactory.eventLoopGroup(1, NETTY_SERVER_BOSS_THREAD_FACTORY_NAME);
        this.workerGroup = NettyEventLoopFactory.eventLoopGroup(this.transportServerConfig.getIoLoopThreadNum(), NETTY_SERVER_IO_WORKER_THREAD_FACTORY_NAME);
        this.bulletServerHandlerExecutor = new UnorderedThreadPoolEventExecutor(this.transportServerConfig.getBulletTransportServerHandlerThreadNum(),
                new DefaultThreadFactory(BULLET_SERVER_HANDLER_WORKER_THREAD_FACTORY_NAME));
    }


    /**
     * 是否已经绑定监听（等于是否启用）
     *
     * @return
     */
    public boolean isBound() {
        return this.isBound;
    }

}
