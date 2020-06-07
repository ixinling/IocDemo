package com.demo;

import com.demo.common.MyApplicationContext;
import com.demo.product.controller.LoginController;

/**
 * 启动类
 * @author 张新玲
 * @since 2020/3/12 19:59
 */

public class Application {
    public static void main(String[] args) throws Exception {
        //从容器中获取对象（自动首字母小写）
        MyApplicationContext applicationContext=new MyApplicationContext();
        LoginController loginController= (LoginController) applicationContext.getIocBean("LoginController");
        String login=loginController.login();
        System.out.println(login);
    }
}
