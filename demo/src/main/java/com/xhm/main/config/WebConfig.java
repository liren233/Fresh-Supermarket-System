package com.xhm.main.config;

import com.xhm.main.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 你原来的本地图片映射（完全保留）
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("file:D:/一些文件/作业/毕设/vue-shop/shop-front/src/assets/images/");
        // 新增：兼容Spring Boot默认静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    // 跨域配置（解决前后端分离 Session 共享、404问题）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 拦截器配置（你原来的代码完全不动）
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/doLogin",
                        "/api/user/doRegister",
                        "/api/user/logout",
                        "/api/goods/**",
                        "/api/cart/**",
                        "/api/address/**",
                        "/static/**",
                        "/**/*.html", "/**/*.js", "/**/*.css",
                        "/upload/**"
                );
    }
}