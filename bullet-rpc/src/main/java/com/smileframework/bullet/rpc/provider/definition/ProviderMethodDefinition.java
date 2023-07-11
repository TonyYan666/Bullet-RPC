package com.smileframework.bullet.rpc.provider.definition;

import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Getter
@Setter
public class ProviderMethodDefinition {

    /**
     * 提供者定义
     */
    private ProviderDefinition providerDefinition;

    /**
     * 方法名词（这个可以根据annotation自行定义，如果不定义则使用类中实际的方法名称）
     */
    private String methodName;

    /**
     * 提供方法对象（反射）
     */
    private Method providerMethod;

    /**
     * 优先参数实际类型（来自请求convertDesc）
     * 如果头部没有 convertDesc 则依然使用方法定义的类型
     * 使用实际类型，需要输出输入的类必须一致包括包路径，如果类不存在 依然会使用参数中的类型
     * 默认情况为false
     */
    private Boolean preferArgumentsActualTypes;

    /**
     * 是否携带实际返回结果类型 默认情况下为 false
     */
    private Boolean transportResponseType;

    /**
     * 参数类型
     */
    private Type[] argumentsTypes;

    /**
     * 响应返回内容格式
     */
    private int returnContentType = BulletContentType.JSON;

    /**
     * 返回对象类型
     */
    private Type returnType;

}
