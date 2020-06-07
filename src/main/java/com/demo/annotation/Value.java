package com.demo.annotation;

import java.lang.annotation.*;

/**
 * 获取配置文件中的键值对
 * @author 张新玲
 * @since 2020/3/12 20:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Value {
    String value()default "";
}
