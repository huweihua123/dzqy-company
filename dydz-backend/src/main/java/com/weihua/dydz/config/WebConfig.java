package com.weihua.dydz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    /*
     * 注意：SPA fallback 拦截器仅在打包部署（前后端合并为单 JAR）时才需要添加。
     * 开发阶段前端独立运行（Vite dev server），后端只做 API，不需要静态托管。
     */

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
