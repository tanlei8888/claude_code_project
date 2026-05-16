package com.hedgehog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 *
 * <p>注册管理员拦截器并配置上传文件静态资源映射。
 * AdminInterceptor 作用于 /api/admin/** 路径，
 * 但排除 /api/admin/upload/**（图片需要公开访问）。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;
    private final AppProperties appProperties;

    public WebConfig(AdminInterceptor adminInterceptor, AppProperties appProperties) {
        this.adminInterceptor = adminInterceptor;
        this.appProperties = appProperties;
    }

    /**
     * 注册管理员权限拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/upload/**");
    }

    /**
     * 配置文件上传目录的静态资源映射。
     * 访问 /api/admin/upload/xxx → 实际读取 uploads/xxx 文件。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/admin/upload/**")
                .addResourceLocations("file:" + appProperties.getUpload().getPath() + "/");
    }
}
