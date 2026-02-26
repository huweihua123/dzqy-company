package com.weihua.dydz.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    /**
     * 托管前端静态文件（vite build 产物输出到 resources/static）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * SPA fallback：非 /api 开头的请求且不是静态资源，统一转发到 index.html
     * 保证前端 React Router 刷新页面不会 404
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request,
                    HttpServletResponse response,
                    Object handler) throws Exception {
                String path = request.getRequestURI();
                // API 请求、静态资源直接放行
                if (path.startsWith("/api") || path.contains(".")) {
                    return true;
                }
                // 其他路径（前端路由）转发到 index.html
                request.getRequestDispatcher("/index.html").forward(request, response);
                return false;
            }
        });
    }
}
