package com.education.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前项目根目录
        String path = System.getProperty("user.dir");
        
        // 把 URL 中的 /uploads/** 映射到 本地项目根目录下的 /uploads/ 文件夹
        // 注意：file: 后面在 Windows 下需要 ///，Linux 下需要 //，这里为了兼容简单写
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path + "/uploads/");
    }
}
