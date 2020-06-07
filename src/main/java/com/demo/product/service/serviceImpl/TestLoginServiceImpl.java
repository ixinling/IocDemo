package com.demo.product.service.serviceImpl;

import com.demo.annotation.MyService;
import com.demo.product.service.LoginService;

/**
 * @author 张新玲
 * @since 2020/3/14 20:23
 */
@MyService
public class TestLoginServiceImpl implements LoginService {
    @Override
    public String login() {
        return "测试多态情况下依赖注入";
    }
}
