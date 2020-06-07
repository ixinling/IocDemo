package com.demo.annotation;

import java.lang.annotation.*;

/**
 * 注入注解--将需要交给IOC容器管理的类放置 -- 定义在属性上的
 * @author 张新玲
 * @since 2020/3/12 20:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MyAutowired {
    String value()default "";
}
