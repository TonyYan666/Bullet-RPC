package com.smileframework.bullet.rpc.provider.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ProviderDefinition<PROVIDER> {

    /**
     * 提供者名词
     */
    private String providerName;

    /**
     * 提供者类路径
     */
    private Class providerClz;

    /**
     * 提供者实例
     */
    private PROVIDER provider;

    /**
     * 提供者方法定义表
     */
    private Map<String, ProviderMethodDefinition> providerFunctionMap = new ConcurrentHashMap<>();

    /**
     * 获得提供者方法定义
     *
     * @param methodName
     * @return
     */
    public ProviderMethodDefinition findMethodDefinition(String methodName) {
        return this.providerFunctionMap.get(methodName);
    }

}
