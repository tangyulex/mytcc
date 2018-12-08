package com.tyxxp.mytcc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个try方法
 *
 * @author tangyu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Try {

    /**
     * 提交预留资源方法地址，如果使用dubbo这里可以配置方法名
     */
    String confirmAddress();

    /**
     * 回滚预留资源方法地址，如果使用dubbo这里可以配置方法名
     */
    String cancelAddress();

    /**
     * rpc方式
     */
    String remoteType() default "dubbo";
}
