package com.kob.matchingsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import static org.springframework.security.authorization.AuthorizationManagers.anyOf;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * 配置安全过滤器链
     *
     * @param httpSecurity HTTP安全对象
     * @return 安全过滤器链
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(CsrfConfigurer::disable) // 禁用CSRF配置
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 设置会话创建策略为无状态
                .authorizeHttpRequests((authorization) -> authorization // 配置HTTP请求的授权规则
                        .requestMatchers("/player/add", "/player/remove") // 匹配请求路径为"/player/add"和"/player/remove"
                        .access(anyOf(hasIpAddress("127.0.0.1"),hasIpAddress("0:0:0:0:0:0:0:1"))) // 对于匹配到的请求，需要满足具有IP地址"127.0.0.1"或"localhost"的用户才能访问
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() // 对于OPTIONS请求方法的请求，允许所有用户访问
                        .anyRequest().authenticated() // 对于其他请求，需要已认证的用户才能访问
                );

        return httpSecurity.build(); // 返回安全过滤器链
    }

    /**
     * 判断请求的IP地址是否与给定的IP地址匹配
     *
     * @param ipAddress IP地址
     * @return 授权管理器
     */
    private AuthorizationManager<RequestAuthorizationContext> hasIpAddress(String ipAddress) {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipAddress); // 创建IP地址匹配器对象
        /*
        Lambda表达式 (authentication, context) -> {...} 实际上是实现了 AuthorizationManager 接口的 check 方法。
        在这个Lambda表达式中，接口的抽象方法的签名与 AuthorizationManager 的 check 方法相匹配，
        因此它充当了 AuthorizationManager 接口的实现。
        定义：AuthorizationDecision check(Supplier<Authentication> authentication, T object);
        */
        return (authentication, context) -> { // 授权管理器
            HttpServletRequest request = context.getRequest(); // 获取请求对象
            return new AuthorizationDecision(ipAddressMatcher.matches(request)); // 返回授权决策结果
        };
    }
}
