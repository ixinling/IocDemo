package com.demo.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解--数据持久层 定义在类、接口、枚举上的
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MyMapping {
    String value() default "";
}
