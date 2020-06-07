package com.demo.product.service.serviceImpl;

import com.demo.annotation.MyAutowired;
import com.demo.annotation.MyService;
import com.demo.product.dao.LoginMapping;
import com.demo.product.service.LoginService;

/**
 * @author 张新玲
 * @since 2020/3/14 19:44
 */
@MyService(value = "test")
public class LoginServiceImpl implements LoginService {
    @MyAutowired
    private LoginMapping loginMapping;
    @Override
    public String login() {
        return loginMapping.login();
    }

}
