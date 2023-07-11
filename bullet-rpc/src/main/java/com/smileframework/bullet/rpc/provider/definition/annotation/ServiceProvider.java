package com.smileframework.bullet.rpc.provider.definition.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceProvider {

    /**
     * 服务提供者路径
     * @return
     */
    String value() default "";

    /**
     * 是否提供所有方法作为提供者的功能
     * @return
     */
    boolean provideAllMethods() default true;

}
