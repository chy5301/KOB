package com.kob.backend.config;

import org.springframework.context.annotation.Configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Configuration
public class CorsConfig implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 将ServletRequest转换为HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) req;

        // 将ServletResponse转换为HttpServletResponse
        HttpServletResponse response = (HttpServletResponse) res;

        // 获取请求头中的Origin字段
        String origin = request.getHeader("Origin");

        // 如果Origin不为空，则设置响应头中的Access-Control-Allow-Origin字段为Origin的值
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // 获取请求头中的Access-Control-Request-Headers字段
        String headers = request.getHeader("Access-Control-Request-Headers");

        // 如果Access-Control-Request-Headers不为空，则设置响应头中的Access-Control-Allow-Headers
        // 和Access-Control-Expose-Headers字段为Access-Control-Request-Headers的值
        if (headers != null) {
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Expose-Headers", headers);
        }

        // 设置响应头中的Access-Control-Allow-Methods字段为"*"，表示支持所有HTTP方法
        response.setHeader("Access-Control-Allow-Methods", "*");

        // 设置响应头中的Access-Control-Max-Age字段为3600，表示预检请求的缓存有效期为3600秒
        response.setHeader("Access-Control-Max-Age", "3600");

        // 设置响应头中的Access-Control-Allow-Credentials字段为"true"，表示允许发送Cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 继续过滤器链的执行
        chain.doFilter(request, response);
    }


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

//作者：yxc
//链接：https://www.acwing.com/file_system/file/content/whole/index/content/5997810/
//来源：AcWing
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。