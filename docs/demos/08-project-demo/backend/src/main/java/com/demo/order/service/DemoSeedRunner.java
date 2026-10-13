package com.demo.order.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时按配置写入示例订单。测试配置关闭该开关。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@Component
public class DemoSeedRunner implements ApplicationRunner {

    /** 示例数据。 */
    private final DemoDataService demoDataService;

    /** 为 true 时，没有未删除订单才写入示例。 */
    private final Boolean seedOnStartup;

    /**
     * @param demoDataService 示例数据
     * @param seedOnStartup 是否在启动时写入示例
     */
    public DemoSeedRunner(DemoDataService demoDataService,
            @Value("${app.demo.seed-on-startup:true}") boolean seedOnStartup) {
        this.demoDataService = demoDataService;
        this.seedOnStartup = Boolean.valueOf(seedOnStartup);
    }

    /**
     * 开关打开且表里没有未删除订单时写入 8 条示例。
     *
     * @param args 启动参数，这里不用
     */
    @Override
    public void run(ApplicationArguments args) {
        if (Boolean.TRUE.equals(seedOnStartup)) {
            demoDataService.seedIfEmpty();
        }
    }
}
