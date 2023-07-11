package com.smileframework.bullet.transport.common.netty.loop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;

import java.util.concurrent.ThreadFactory;

/**
 * Netty Event Loop 工厂
 */
public class NettyEventLoopFactory {

    private static final String OS_LINUX_PREFIX = "linux";

    private static final String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";

    private static final String OS_NAME_KEY = "os.name";

    /**
     * 根据当前系统环境选择EventLoopGroup 实现类
     *
     * @param threads
     * @param threadFactoryName
     * @return
     */
    public static EventLoopGroup eventLoopGroup(int threads, String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll() ? new EpollEventLoopGroup(threads, threadFactory) :
                new NioEventLoopGroup(threads, threadFactory);
    }

    public static EventExecutor eventExecutor(String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return new DefaultEventExecutor(threadFactory);
    }

    private static boolean shouldEpoll() {
        if (Boolean.parseBoolean(System.getProperty(NETTY_EPOLL_ENABLE_KEY, "false"))) {
            String osName = System.getProperty(OS_NAME_KEY);
            return osName.toLowerCase().contains(OS_LINUX_PREFIX) && Epoll.isAvailable();
        }
        return false;
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return shouldEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

}
