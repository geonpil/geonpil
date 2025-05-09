package com.geonpil.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestUriInterceptor())
                .addPathPatterns("/**"); // 모든 요청에 적용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 실행 디렉토리 하위의 upload 폴더를 /upload/** URL로 매핑
        String uploadPath = System.getProperty("user.dir") + "/upload/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
    }

}