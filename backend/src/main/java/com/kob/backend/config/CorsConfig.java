package com.kob.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     * 配置跨域请求过滤器
     * @return 跨域请求过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        // 创建一个UrlBasedCorsConfigurationSource对象，用于存储跨域资源配置
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        // 创建一个CorsConfiguration对象，用于配置跨域请求的允许条件
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 设置允许跨域请求包含凭证（如cookie）
        corsConfiguration.setAllowCredentials(true);
        // 设置允许所有域名进行跨域请求
        corsConfiguration.addAllowedOriginPattern("*");
        // 设置允许所有请求头部信息进行跨域请求
        corsConfiguration.addAllowedHeader("*");
        // 设置允许所有HTTP请求方法进行跨域请求
        corsConfiguration.addAllowedMethod("*");

        // 注册跨域资源配置，将上述配置应用到所有URL
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        // 创建一个CorsFilter对象，用于过滤跨域请求并返回相应的响应头
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}