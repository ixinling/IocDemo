package com.demo.product.controller;

import com.demo.annotation.MyAutowired;
import com.demo.annotation.MyController;
import com.demo.annotation.Value;
import com.demo.product.service.LoginService;

/**
 * @author 张新玲
 * @since 2020/3/14 19:43
 */

@MyController
public class LoginController {
    @Value(value = "IocDemo.scan.pathTest")
    private String test;

    @MyAutowired(value = "test")
    private LoginService loginService;

    public String login(){
        return loginService.login();
    }
}
