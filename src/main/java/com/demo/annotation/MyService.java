package com.demo.annotation;

import java.lang.annotation.*;

/**
 * 服务层--定义在 类、接口、枚举上的
 * @author 张新玲
 * @since 2020/3/12 20:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MyService {
    String value()default "";
}
