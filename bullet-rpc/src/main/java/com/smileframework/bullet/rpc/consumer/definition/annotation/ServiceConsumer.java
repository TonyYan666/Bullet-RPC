package com.smileframework.bullet.rpc.consumer.definition.annotation;

import com.smileframework.bullet.transport.common.protocol.header.BulletContentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceConsumer {

    /**
     * 提供者服务地址 bullet://test-service
     *
     * @return
     */
    String serverAddress();

    /**
     * 提供者路径 /ProductService 为空则默认使用类名称
     *
     * @return
     */
    String providerPath() default "";

    /**
     * 接口调用超时时间
     *
     * @return
     */
    int requestTimeoutMills() default 3000;

    /**
     * 请求内容类型
     *
     * @return
     */
    int requestContentType() default BulletContentType.JSON;

    /**
     * 降级处理类 可以是 consumer 接口的实现 也可以是 ConsumerFallbackHandler 的实现
     *
     * @return
     */
    Class fallback() default Void.class;


}
