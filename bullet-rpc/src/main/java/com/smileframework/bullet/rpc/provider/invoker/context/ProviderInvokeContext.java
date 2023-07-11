package com.smileframework.bullet.rpc.provider.invoker.context;

import com.smileframework.bullet.rpc.provider.definition.ProviderMethodDefinition;
import com.smileframework.bullet.transport.common.protocol.BulletRequest;
import com.smileframework.bullet.transport.common.protocol.BulletResponse;
import com.smileframework.bullet.transport.server.connection.ClientConnection;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法调用上下文
 */
@Getter
@Setter
public class ProviderInvokeContext {

    /**
     * 返回可以额外携带的元数据
     */
    private Map<String, Object> responseMeta = new ConcurrentHashMap<>();

    /**
     * 调用方法定义
     */
    private ProviderMethodDefinition methodDefinition;

    /**
     * 客户端连接
     */
    private ClientConnection clientConnection;

    /**
     * 请求对象
     */
    private BulletRequest<Object[]> request;

    /**
     * 响应对象
     */
    private BulletResponse<?> response;

    /**
     * 执行线程
     */
    private Thread workThread;

    /**
     * 调用时间
     */
    private Long startInvokeTime;

    /**
     * 添加响应元数据
     */
    public void addMeta(String metaKey, Object value) {
        this.responseMeta.put(metaKey, value);
    }

    /**
     * 获得响应元数据
     *
     * @param metaKey
     * @param <T>
     * @return
     */
    public <T> T getMeta(String metaKey) {
        return (T) this.responseMeta.get(metaKey);
    }

}
