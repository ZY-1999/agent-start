package com.demo.order.web;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * 非 /api 的未知路径回退到前端入口页。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@Component
public class SpaResourceConfig implements WebMvcConfigurer {

    /**
     * 已有静态资源按原路径返回；其余非接口路径返回入口文档。
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").resourceChain(true)
            .addResolver(new PathResourceResolver() {
                /**
                 * 接口路径不回退；静态文件存在就返回它，否则返回入口页。
                 *
                 * @param resourcePath 请求路径
                 * @param location 静态资源根
                 * @return 命中的资源；接口路径返回空
                 * @throws IOException 读取资源失败时抛出
                 */
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    if (resourcePath.startsWith("api/") || "api".equals(resourcePath)) {
                        return null;
                    }
                    Resource existing = location.createRelative(resourcePath);
                    if (existing.exists() && existing.isReadable()) {
                        return existing;
                    }
                    return location.createRelative("index.html");
                }
            });
    }
}
