package com.smileframework.bullet.rpc.consumer.definition;

import com.smileframework.bullet.transport.common.exception.rpc.consumer.ConsumerDefinitionException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ConsumerDefinition {

    /**
     * RPC 方法提供者地址
     */
    private URI serverAddress;

    /**
     * 提供者路径
     */
    private String providerPath;

    /**
     * RPC消费者接口
     */
    private Class consumerInterface;

    /**
     * 降级处理器
     */
    private Object consumerFallbackHandler;


    /**
     * 接口功能定义
     */
    private Map<String, ConsumerMethodDefinition> methodNameWithDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 接口方法映射
     */
    private Map<Method, ConsumerMethodDefinition> methodDefinitionMap = new ConcurrentHashMap<>();


    public ConsumerMethodDefinition findMethodDefinitionByName(String methodName) {
        return this.methodNameWithDefinitionMap.get(methodName);
    }

    public ConsumerMethodDefinition findMethodDefinition(Method method) {
        return this.methodDefinitionMap.get(method);
    }

    public void addMethodDefinition(ConsumerMethodDefinition methodDefinition) {
        if (this.methodNameWithDefinitionMap.containsKey(methodDefinition.getMethodName())) {
            throw new ConsumerDefinitionException("Bullet RPC Consumer method name must be unique."
                    + consumerInterface.getName() + "#"
                    + methodDefinition.getMethodName() + " already exists.");
        }
        this.methodNameWithDefinitionMap.put(methodDefinition.getMethodName(), methodDefinition);
        this.methodDefinitionMap.put(methodDefinition.getMethod(), methodDefinition);
    }

    public int getMethodCount() {
        return this.methodNameWithDefinitionMap.size();
    }

}
