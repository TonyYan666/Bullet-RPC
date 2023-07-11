package com.smileframework.bullet.rpc.provider.definition.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceProviderMethod {

    /**
     * 默认按照方法名作为提供者功能名词
     *
     * @return
     */
    String value() default "";

    /**
     * 是否传送返回的实际类型
     *
     * @return
     */
    boolean transportResponseType() default false;

    /**
     * 优先参数实际类型（来自请求convertDesc）
     * 如果头部没有 convertDesc 则依然使用方法定义的类型
     * 使用实际类型，需要输出输入的类必须一致包括包路径，如果类不存在 依然会使用参数中的类型
     * 默认情况为false
     * 如果实际类型如定义的类型不一致或者不是实际类型的基类，则会抛出参数不一致的问题
     *
     * @return
     */
    boolean preferArgumentsActualTypes() default false;


}
