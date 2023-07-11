package com.smileframework.bullet.transport.client.netty;

import com.smileframework.bullet.transport.client.connection.AbstractConnection;
import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import com.smileframework.bullet.transport.client.netty.codec.BulletRequestProtocolEncoder;
import com.smileframework.bullet.transport.client.netty.codec.BulletResponseProtocolDecoder;
import com.smileframework.bullet.transport.client.netty.codec.TransportRequestEncoder;
import com.smileframework.bullet.transport.client.netty.codec.TransportResponseDecoder;
import com.smileframework.bullet.transport.common.netty.frame.BulletFrameDecoder;
import com.smileframework.bullet.transport.common.netty.frame.BulletFrameEncoder;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    public static final int HEARTBEAT_TIME = 45;

    public static final int HEARTBEAT_WAIT_RESPONSE_TIMEOUT = 5;

    private ServerConnectionProperties clientConfig;

    private Channel clientChannel;

    private EventLoopGroup loopGroup;

    private AbstractConnection serverConnection;


    public NettyClient(ServerConnectionProperties clientConfig, AbstractConnection serverConnection) {
        this.clientConfig = clientConfig;
        this.serverConnection = serverConnection;
    }

    public void openConnection() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        this.loopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(this.loopGroup);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new BulletFrameDecoder());
                    pipeline.addLast(new BulletFrameEncoder());
                    pipeline.addLast(new TransportRequestEncoder());
                    pipeline.addLast(new BulletRequestProtocolEncoder());
                    pipeline.addLast(new TransportResponseDecoder());
                    pipeline.addLast(new BulletResponseProtocolDecoder());
                    pipeline.addLast(new IdleStateHandler(HEARTBEAT_TIME + HEARTBEAT_WAIT_RESPONSE_TIMEOUT,
                            HEARTBEAT_TIME,
                            HEARTBEAT_TIME + HEARTBEAT_WAIT_RESPONSE_TIMEOUT, TimeUnit.SECONDS));
                    pipeline.addLast(NettyClient.this.serverConnection);
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(this.clientConfig.getServerAddress(), this.clientConfig.getServerPort());
            channelFuture.sync();
            this.clientChannel = channelFuture.channel();
        } catch (Exception e) {
            this.disconnect();
            throw e;
        }
    }



    public void writeAndFlush(BulletRequest<byte[]> request) {
        this.clientChannel.writeAndFlush(request);
    }

    public void disconnect() {
        try {
            if (clientChannel != null) {
                clientChannel.disconnect().sync();
            }
            if (this.loopGroup != null) {
                long timeout = this.clientConfig.getShutdownTimeout().getSeconds();
                this.loopGroup.shutdownGracefully(0, timeout, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.warn("Bullet bullet transport client disconnecting interrupted.");
        } finally {
            if (this.loopGroup != null) {
                this.loopGroup.shutdownGracefully();
            }
        }
    }

}