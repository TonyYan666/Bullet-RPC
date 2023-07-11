package com.smileframework.bullet.rpc.provider.definition;

import cn.hutool.core.util.StrUtil;
import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProvider;
import com.smileframework.bullet.rpc.provider.definition.annotation.ServiceProviderMethod;
import com.smileframework.bullet.transport.common.exception.rpc.provider.BulletProviderDefinitionException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 服务提供者定义构建器
 *
 * @param <PROVIDER>
 */
public class ProviderDefinitionBuilder<PROVIDER> {

    /**
     * 提供者定义对象
     */
    private ProviderDefinition<PROVIDER> providerDefinition;

    /**
     * 提供者实际对象
     */
    private PROVIDER provider;

    /**
     * 实际的提供者类型
     */
    private Class targetClz;

    /**
     * 提供者annotation
     */
    private ServiceProvider providerAnnotation;

    /**
     * 初始化
     *
     * @param provider 提供者实例
     */
    private ProviderDefinitionBuilder(PROVIDER provider) {
        this.providerDefinition = new ProviderDefinition<>();
        this.provider = provider;
        this.targetClz = this.provider.getClass();
        this.providerAnnotation = (ServiceProvider) targetClz.getDeclaredAnnotation(ServiceProvider.class);
    }

    /**
     * 初始化
     *
     * @param provider 提供者实例
     */
    private ProviderDefinitionBuilder(PROVIDER provider, Class targetClz) {
        this.providerDefinition = new ProviderDefinition<>();
        this.provider = provider;
        this.targetClz = targetClz;
        this.providerAnnotation = (ServiceProvider) targetClz.getDeclaredAnnotation(ServiceProvider.class);
    }


    /**
     * 创建方法
     *
     * @param provider
     * @param <PROVIDER>
     * @return
     */
    public static <PROVIDER> ProviderDefinitionBuilder<PROVIDER> create(PROVIDER provider) {
        if (provider == null) {
            throw new BulletProviderDefinitionException("Provider could not be null.");
        }
        return new ProviderDefinitionBuilder<>(provider);
    }

    /**
     * 创建方法
     *
     * @param provider
     * @param <PROVIDER>
     * @return
     */
    public static <PROVIDER> ProviderDefinitionBuilder<PROVIDER> create(PROVIDER provider, Class targetClz) {
        if (provider == null) {
            throw new BulletProviderDefinitionException("Provider could not be null.");
        }
        return new ProviderDefinitionBuilder<>(provider, targetClz);
    }


    /**
     * 构建提供者定义对象
     *
     * @return
     */
    public ProviderDefinition<PROVIDER> build() {
        this.injectServiceInstance();
        this.defineProviderName();
        this.scanProviderMethods();
        return this.providerDefinition;
    }

    /**
     * 注入实际的服务实例
     */
    private void injectServiceInstance() {
        this.providerDefinition.setProvider(this.provider);
        this.providerDefinition.setProviderClz(this.targetClz);
    }

    /**
     * 定义服务提供者路径（名称）
     */
    private void defineProviderName() {
        this.providerDefinition.setProviderName("/" + this.targetClz.getSimpleName());
        if (this.providerAnnotation != null && StrUtil.isNotEmpty(this.providerAnnotation.value())) {
            String providerName = this.providerAnnotation.value();
            if (providerName.indexOf("/") != 0) {
                providerName = "/" + providerName;
            }
            providerName = providerName.replace("/+", "/");
            this.providerDefinition.setProviderName(providerName);
        }
    }

    /**
     * 扫描所有提供者方法列表
     */
    private void scanProviderMethods() {
        Method[] methods = targetClz.getMethods();
        if (methods == null || methods.length <= 0) {
            throw new BulletProviderDefinitionException("Provider methods must provide.");
        }
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (this.isBasicMethod(method)) {
                continue;
            }
            this.injectProviderMethodDefinition(method);
        }
        if (this.providerDefinition.getProviderFunctionMap().isEmpty()) {
            throw new BulletProviderDefinitionException("Provider method must provide.");
        }
    }


    public boolean isBasicMethod(Method method) {
        switch (method.getName()) {
            case "getClass":
            case "wait":
            case "notifyAll":
            case "notify":
            case "hashCode":
            case "equals":
            case "toString":
                return true;
            default:
                return false;
        }
    }

    /**
     * 注入服务提供者方法定义
     *
     * @param method
     */
    private void injectProviderMethodDefinition(Method method) {
        ServiceProviderMethod annotation = method.getDeclaredAnnotation(ServiceProviderMethod.class);
        if (annotation == null && !this.providerAnnotation.provideAllMethods()) {
            return;
        }
        ProviderMethodDefinition functionDefinition = new ProviderMethodDefinition();
        functionDefinition.setProviderMethod(method);
        functionDefinition.setProviderDefinition(this.providerDefinition);
        functionDefinition.setArgumentsTypes(method.getGenericParameterTypes());
        functionDefinition.setReturnType(method.getGenericReturnType());
        functionDefinition.setPreferArgumentsActualTypes(Boolean.FALSE);
        functionDefinition.setTransportResponseType(Boolean.FALSE);
        functionDefinition.setMethodName(method.getName());
        if (annotation != null) {
            if (StrUtil.isNotBlank(annotation.value())) {
                functionDefinition.setMethodName(annotation.value());
            }
            functionDefinition.setTransportResponseType(annotation.transportResponseType());
            functionDefinition.setPreferArgumentsActualTypes(annotation.preferArgumentsActualTypes());
        }
        if (this.providerDefinition.getProviderFunctionMap().containsKey(functionDefinition.getMethodName())) {
            throw new BulletProviderDefinitionException("Bullet RPC Provider method name must be unique. "
                    + this.providerDefinition.getProviderClz().getName() + "#"
                    + functionDefinition.getMethodName() + " already exists.");
        }
        this.providerDefinition.getProviderFunctionMap().put(functionDefinition.getMethodName(), functionDefinition);
    }


}
