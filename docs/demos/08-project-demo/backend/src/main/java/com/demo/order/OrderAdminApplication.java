package com.demo.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单管理应用入口。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@SpringBootApplication
public class OrderAdminApplication {

    /**
     * 启动应用。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderAdminApplication.class, args);
    }
}
