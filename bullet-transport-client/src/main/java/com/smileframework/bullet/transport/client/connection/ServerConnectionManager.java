package com.smileframework.bullet.transport.client.connection;

import com.smileframework.bullet.transport.client.BulletClientContext;
import com.smileframework.bullet.transport.client.config.BulletClientConfig;
import com.smileframework.bullet.transport.client.connection.dto.AddressAndPort;
import com.smileframework.bullet.transport.client.connection.properties.ServerConnectionProperties;
import com.smileframework.bullet.transport.client.connection.lifecycle.BulletTransportClientLifecycle;
import com.smileframework.bullet.transport.common.exception.config.ClientContextNotReadyException;
import com.smileframework.bullet.transport.common.exception.transport.client.BulletClientConnectException;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ServerConnectionManager implements BulletTransportClientLifecycle {

    public static final String CLIENT_ATTR_ADDRESS_AND_PORT = "ADDRESS_PORT";

    private BulletClientContext context;

    private Map<String, ServerConnection> connectionMap = new ConcurrentHashMap<>();

    private Map<String, CompletableFuture<ServerConnection>> connectingFutureMap = new ConcurrentHashMap<>();

    private AtomicBoolean isShutdown = new AtomicBoolean(false);

    private ReentrantLock connectionLock = new ReentrantLock();

    private Thread idleConnectionRecycleDaemon;

    public void setContext(BulletClientContext context) {
        this.context = context;
    }

    /**
     * 通过单个URI 获得多个Connection 连接为了实现单服务并发请求
     *
     * @param uri
     * @param request
     * @param ignoreConnectionError
     * @return
     */
    public List<ServerConnection> getServerConnections(URI uri, BulletRequest<?> request, boolean ignoreConnectionError) {
        if (this.isShutdown.get()) {
            throw new BulletClientConnectException("Transport client manager was shutdown.");
        }
        this.check();
        List<AddressAndPort> addressAndPorts = this.getRealAddressAndPortList(uri, request);
        return this.getServerConnections(addressAndPorts, ignoreConnectionError);
    }

    /**
     * 获得同一个集群网络下所有连接（可能会比较耗时）
     *
     * @param request
     * @param ignoreConnectionError
     * @return
     */
    public List<ServerConnection> getAllServerConnections(BulletRequest<?> request, boolean ignoreConnectionError) {
        if (this.isShutdown.get()) {
            throw new BulletClientConnectException("Transport client manager was shutdown.");
        }
        this.check();
        List<AddressAndPort> addressAndPorts = this.getAllServersAddressAndPort(request);
        return this.getServerConnections(addressAndPorts, ignoreConnectionError);
    }

    /**
     * 获得一个连接服务端的连接
     *
     * @param uri
     * @return
     */
    public ServerConnection getServerConnection(URI uri, BulletRequest<?> request) {
        if (this.isShutdown.get()) {
            throw new BulletClientConnectException("Transport client manager was shutdown.");
        }
        this.check();
        AddressAndPort address = this.getRealAddressAndPort(uri, request);
        return this.getServerConnection(address);
    }

    /**
     * 获得或创建多个连接
     *
     * @param addressAndPortList
     * @return
     */
    protected List<ServerConnection> getServerConnections(List<AddressAndPort> addressAndPortList, boolean ignoreConnectionError) {
        List<ServerConnection> serverConnections = new ArrayList<>();
        for (AddressAndPort addressAndPort : addressAndPortList) {
            try {
                ServerConnection serverConnection = this.getServerConnection(addressAndPort);
                serverConnections.add(serverConnection);
            } catch (Exception e) {
                if (ignoreConnectionError) {
                    log.error("[Bullet-transport-client] could not get connection to " + addressAndPort + ".", e);
                } else {
                    throw e;
                }
            }
        }
        return serverConnections;
    }

    /**
     * 获得或创建连接
     *
     * @param address
     * @return
     */
    protected ServerConnection getServerConnection(AddressAndPort address) {
        ServerConnection connection = this.connectionMap.get(address.toString());
        if (connection != null) {
            return connection;
        }
        CompletableFuture<ServerConnection> connectingFuture = this.connectingFutureMap.get(address.toString());
        boolean doConnect = false;
        if (connectingFuture == null) {
            this.connectionLock.lock();
            try {
                connection = this.connectionMap.get(address.toString());
                if (connection != null) {
                    return connection;
                }
                connectingFuture = this.connectingFutureMap.get(address.toString());
                if (connectingFuture == null) {
                    connectingFuture = new CompletableFuture<>();
                    this.connectingFutureMap.put(address.toString(), connectingFuture);
                    doConnect = true;
                }
            } finally {
                this.connectionLock.unlock();
            }
        }
        if (doConnect) {
            try {
                connection = this.createServerConnection(address);
                connectingFuture.complete(connection);
                this.connectingFutureMap.remove(address.toString());
                return connection;
            } catch (Exception e) {
                connectingFuture.completeExceptionally(e);
                this.connectingFutureMap.remove(address.toString());
                throw e;
            }
        }
        try {
            return connectingFuture.get(this.context.getClientConfig().getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw new BulletClientConnectException("Bullet client connection catch an execution error.", e);
        } catch (InterruptedException e) {
            throw new BulletClientConnectException("Bullet client connection was interrupted.", e);
        } catch (TimeoutException e) {
            throw new BulletClientConnectException("Bullet client connection waiting timeout error.", e);
        }
    }


    /**
     * 创建一个服务端的连接
     *
     * @param address
     * @return
     */
    protected ServerConnection createServerConnection(AddressAndPort address) {
        ServerConnectionProperties connectionProperties = this.createConnectionPropertiesByConfig(address);
        ServerConnection connection = new ServerConnection(connectionProperties);
        this.initServerConnection(connection);
        connection.connect();
        connection.setAttribute(CLIENT_ATTR_ADDRESS_AND_PORT, address.toString());
        connection.connectionHandshake();
        this.connectionMap.put(address.toString(), connection);
        log.info("[Bullet-Client] New server connected " + address);
        return connection;
    }

    /**
     * 获得一个服务端连接基本参数属性（基于全局配置）
     *
     * @param address
     * @return
     */
    protected ServerConnectionProperties createConnectionPropertiesByConfig(AddressAndPort address) {
        BulletClientConfig clientConfig = this.context.getClientConfig();
        ServerConnectionProperties connectionProperties = new ServerConnectionProperties();
        connectionProperties.setServerPort(address.getPort());
        connectionProperties.setServerAddress(address.getAddress());
        connectionProperties.setIdleTimeout(clientConfig.getIdleTimeout());
        connectionProperties.setShutdownTimeout(clientConfig.getShutdownTimeout());
        connectionProperties.setHandshakeTimeout(clientConfig.getHandshakeTimeout());
        connectionProperties.setHeartbeatTimeout(clientConfig.getHeartbeatTimeout());
        return connectionProperties;
    }

    /**
     * 初始化连接所需的基础组件
     *
     * @param connection
     */
    protected void initServerConnection(ServerConnection connection) {
        connection.setLifecycle(this);
        connection.setHandshakeInfoProvider(this.context.getHandshakeInfoProvider());
        connection.setContentConvertManager(this.context.getContentConvertManager());
        connection.setErrorHandle(this.context.getResponseErrorHandler());
    }

    /**
     * 根据URI获得需要建立连接的地址与端口
     * （如果基于微服务，可以根据host自动匹配对应的负载均衡获得具体的地址）
     *
     * @param originalUri
     * @return
     */
    protected AddressAndPort getRealAddressAndPort(URI originalUri, BulletRequest<?> request) {
        String address = originalUri.getHost();
        int port = originalUri.getPort();
        if (port <= 0) {
            port = 2186;
        }
        return new AddressAndPort(address, port);
    }


    /**
     * 通过URI 获得多个实际地址（微服务集群环境下）
     *
     * @param originalUri
     * @param request
     * @return
     */
    protected List<AddressAndPort> getRealAddressAndPortList(URI originalUri, BulletRequest<?> request) {
        AddressAndPort addressAndPort = this.getRealAddressAndPort(originalUri, request);
        List<AddressAndPort> list = new ArrayList<>();
        list.add(addressAndPort);
        return list;
    }

    /**
     * 获得所有服务连接
     *
     * @param request
     * @return
     */
    protected List<AddressAndPort> getAllServersAddressAndPort(BulletRequest<?> request) {
        throw new BulletClientConnectException("No support to get all server connection address in the same network system by basic server connection manager.");
    }


    /**
     * 连接声明周期回调（连接成功）
     *
     * @param connection
     */
    @Override
    public void connected(AbstractConnection connection) {
    }

    /**
     * 连接声明周期回调（连接断开）
     *
     * @param connection
     */
    @Override
    public void disconnected(AbstractConnection connection) {
        String key = connection.getAttribute(CLIENT_ATTR_ADDRESS_AND_PORT);
        if (key != null) {
            this.connectionMap.remove(key);
        }
        log.info("[Bullet-Client] " + key + " server connection has been disconnected.");
    }

    /**
     * 关闭所有持有的连接
     */
    public void shutdown() {
        this.stopIdleConnectionRecycle();
        this.isShutdown.set(true);
        for (ServerConnection serverConnection : this.connectionMap.values()) {
            try {
                serverConnection.disconnect();
            } catch (Exception e) {
                log.warn("client disconnect throw an exception.", e);
            }
        }
    }

    /**
     * 客户端上下文检查
     */
    public void check() {
        if (this.context == null) {
            throw new ClientContextNotReadyException("Client context not ready.");
        }
        this.context.isReadyCheck();
    }

    public void stopIdleConnectionRecycle() {
        if (this.idleConnectionRecycleDaemon == null) {
            return;
        }
        this.idleConnectionRecycleDaemon.interrupt();
        this.idleConnectionRecycleDaemon = null;
    }

    public void startIdleConnectionRecycle() {
        this.stopIdleConnectionRecycle();
        this.idleConnectionRecycleDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                long idleTimeout = context.getClientConfig().getIdleTimeout().toMillis();
                while (true) {
                    if (Thread.interrupted()) {
                        break;
                    }
                    for (ServerConnection connection : connectionMap.values()) {
                        if (System.currentTimeMillis() - connection.getLastCommunicatedTime() > idleTimeout) {
                            connection.disconnect();
                        }
                    }
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        this.idleConnectionRecycleDaemon.start();
    }

}
