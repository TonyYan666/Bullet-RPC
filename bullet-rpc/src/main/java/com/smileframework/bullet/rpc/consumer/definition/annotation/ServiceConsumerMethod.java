package com.smileframework.bullet.rpc.consumer.definition.annotation;

import com.smileframework.bullet.rpc.consumer.definition.constant.RequestMode;
import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceConsumerMethod {

    /**
     * 默认按照方法名作为提供者功能名词
     *
     * @return
     */
    String name() default "";

    /**
     * 接口调用超时时间
     *
     * @return
     */
    int requestTimeoutMills() default 3000;

    /**
     * 是否为通知方法（通知方法没有任何返回完全异步，但是在建立连接的过程是同步的）
     *
     * @return
     */
    boolean isNotification() default false;

    /**
     * 请求内容类型
     *
     * @return
     */
    int requestContentType() default BulletContentType.JSON;

    /**
     * 请求模式：默认单播，在多播模式下 只能使用 notification 通知模式
     */
    RequestMode requestMode() default RequestMode.UNICAST;

    /**
     * 重试次数
     *
     * @return
     */
    int retry() default 0;

    /**
     * 重试间隔（毫秒）
     *
     * @return
     */
    int retryIntervalMs() default 20;

    /**
     * 优先返回值的实际类型（来自返回头部convertDesc）
     * 如果头部没有 convertDesc 则依然使用方法定义的类型
     * 使用实际类型，需要输出输入的类必须一致包括包路径，如果类不存在 依然会使用参数中的类型
     * 默认情况为false
     */
    boolean preferResponseActualType() default false;


    /**
     * 是否携带实际请求参数结果类型 默认情况下为false
     */
    boolean transportArgumentsTypes() default false;


}
