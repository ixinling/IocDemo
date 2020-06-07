package com.demo.product.dao;

import com.demo.annotation.MyMapping;

/**
 * @author 张新玲
 * @since 2020/3/14 20:24
 */
@MyMapping
public class LoginMapping {
    public String login(){
        return "项目启动成功";
    }
}
