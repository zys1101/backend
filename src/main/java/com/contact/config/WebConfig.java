package com.contact.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 配置静态资源访问
 *
 * @author Contact Manager
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.avatar-path:uploads/avatar/}")
    private String avatarPath;

    @Value("${file.upload.path:uploads/contact/}")
    private String contactPath;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/avatar/**")
                .addResourceLocations("file:" + avatarPath);
        registry.addResourceHandler("/uploads/contact/**")
                .addResourceLocations("file:" + contactPath);
    }
}
