package com.smileframework.bullet.rpc.provider.definition;


import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderRegistryException;

import java.nio.file.ProviderNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProviderDefinitionManager {

    private final Map<String, ProviderDefinition> providerDefinitionMap = new ConcurrentHashMap<>();

    private final Map<Object, ProviderDefinition> providerWithDefinitionMapper = new ConcurrentHashMap<>();

    private final Map<String, ProviderMethodDefinition> actionURLWithMethodDefinitionMapper = new ConcurrentHashMap<>();

    /**
     * 提供者注册
     *
     * @param provider
     * @param <T>
     */
    public <T> void providerRegister(T provider) {
        if (providerWithDefinitionMapper.containsKey(provider)) {
            return;
        }
        ProviderDefinition<T> providerDefinition = ProviderDefinitionBuilder.create(provider).build();
        this.providerRegister(providerDefinition);
    }

    /**
     * 提供者注册
     *
     * @param provider
     * @param <T>
     */
    public <T> void providerRegister(T provider, Class<?> targetClz) {
        if (providerWithDefinitionMapper.containsKey(provider)) {
            return;
        }
        ProviderDefinition<T> providerDefinition = ProviderDefinitionBuilder.create(provider, targetClz).build();
        this.providerRegister(providerDefinition);
    }

    /**
     * 提供者注册
     *
     * @param providerDefinition
     * @param <T>
     */
    public <T> void providerRegister(ProviderDefinition<T> providerDefinition) {
        if (providerDefinitionMap.containsKey(providerDefinition.getProviderName())) {
            if (providerDefinitionMap.get(providerDefinition.getProviderName()).getProvider() != providerDefinition.getProvider()) {
                throw new BulletProviderRegistryException("provider name " + providerDefinition.getProviderName() + " has already exist.");
            }
            return;
        }
        providerDefinitionMap.put(providerDefinition.getProviderName(), providerDefinition);
        providerWithDefinitionMapper.put(providerDefinition.getProvider(), providerDefinition);
    }

    /**
     * 获得方法定义
     *
     * @param actionURL
     * @return
     */
    public ProviderMethodDefinition findProviderMethodDefinition(String actionURL) {
        if (actionURL == null) {
            throw new ProviderNotFoundException("Bullet request action could not be null.");
        }
        actionURL = actionURL.trim();
        String[] actionParts = actionURL.split("#");
        if (actionParts.length != 2) {
            throw new ProviderNotFoundException("Bullet request action could not be parsed.");
        }
        ProviderDefinition<Object> providerDefinition = this.getProviderByName(actionParts[0]);
        if (providerDefinition == null) {
            throw new ProviderNotFoundException("Bullet request action " + actionURL + " provider not found.");
        }
        ProviderMethodDefinition methodDefinition = providerDefinition.findMethodDefinition(actionParts[1]);
        if (methodDefinition == null) {
            throw new ProviderNotFoundException("Bullet request action " + actionURL + " provider function not found.");
        }
        return methodDefinition;
    }

    /**
     * 获得方法定义，缓存优先
     *
     * @param actionURL
     * @return
     */
    public ProviderMethodDefinition findProviderMethodDefinitionByCache(String actionURL) {
        if (actionURL == null) {
            throw new ProviderNotFoundException("Bullet request action could not be null.");
        }
        actionURL = actionURL.trim();
        ProviderMethodDefinition methodDefinition = this.actionURLWithMethodDefinitionMapper.get(actionURL);
        if (methodDefinition != null) {
            return methodDefinition;
        }
        synchronized (this.actionURLWithMethodDefinitionMapper) {
            methodDefinition = this.actionURLWithMethodDefinitionMapper.get(actionURL);
            if (methodDefinition != null) {
                return methodDefinition;
            }
            methodDefinition = this.findProviderMethodDefinition(actionURL);
            if (methodDefinition != null) {
                this.actionURLWithMethodDefinitionMapper.put(actionURL, methodDefinition);
            }
        }
        return methodDefinition;
    }


    /**
     * 获得提供者定义
     *
     * @param providerName
     * @param <T>
     * @return
     */
    public <T> ProviderDefinition<T> getProviderByName(String providerName) {
        ProviderDefinition<T> providerDefinition = this.providerDefinitionMap.get(providerName);
        return providerDefinition;
    }

    /**
     * 关闭提供者
     *
     * @param providerName
     */
    public void providerShutdown(String providerName) {
        ProviderDefinition providerDefinition = this.providerDefinitionMap.get(providerName);
        if (providerDefinition == null) {
            return;
        }
        this.providerWithDefinitionMapper.remove(providerDefinition.getProvider());
        this.providerDefinitionMap.remove(providerDefinition.getProviderName());
    }

    /**
     * 关闭提供者
     *
     * @param provider
     * @param <T>
     */
    public <T> void providerShutdown(T provider) {
        ProviderDefinition<T> providerDefinition = this.providerWithDefinitionMapper.get(provider);
        if (providerDefinition == null) {
            return;
        }
        this.providerWithDefinitionMapper.remove(provider);
        this.providerDefinitionMap.remove(providerDefinition.getProviderName());
    }

}
