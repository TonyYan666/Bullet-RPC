package com.smileframework.bullet.rpc.consumer.definition;

import com.smileframework.bullet.rpc.consumer.definition.ConsumerDefinition;
import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;

@Setter
@Getter
public class ConsumerMethodDefinition {

    /**
     * 消费者定义
     */
    private ConsumerDefinition consumerDefinition;

    /**
     * 返回类型
     */
    private Type returnType;

    /**
     * 参数类型
     */
    private Type[] argumentsTypes;

    /**
     * 方法名词
     */
    private String methodName;

    /**
     * 是否为通知模式
     */

    private boolean notification = false;

    /**
     * 方法实例
     */
    private Method method;

    /**
     * 请求内容类型
     */
    private int requestContentType = BulletContentType.JSON;

    /**
     * 请求超时时间
     */
    private Duration requestTimeout = Duration.ofSeconds(3);

    /**
     * 请求模式
     */
    private RequestMode requestMode = RequestMode.UNICAST;

    /**
     * 是否重试
     */
    private int retry = 0;

    /**
     * 重试间隔时间（毫秒）
     */
    private int retryIntervalMs = 20;

    /**
     * 是否为异步请求
     */
    private boolean async = false;

    /**
     * 是否reactor mono 响应式调用
     */
    private boolean isMonoReactor = false;

    /**
     * 优先返回值的实际类型（来自返回头部convertDesc）
     * 如果头部没有 convertDesc 则依然使用方法定义的类型
     * 使用实际类型，需要输出输入的类必须一致包括包路径，如果类不存在 依然会使用参数中的类型
     * 默认情况为false
     */
    private Boolean preferResponseActualType;


    /**
     * 是否携带实际请求参数结果类型 默认情况下为false
     */
    private Boolean transportArgumentsTypes;

}
