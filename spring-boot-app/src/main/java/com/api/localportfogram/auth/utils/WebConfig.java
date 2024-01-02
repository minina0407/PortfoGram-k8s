package com.api.localportfogram.auth.utils;

import com.api.localportfogram.common.MdcLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "PUT", "POST", "DELETE");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MdcLoggingInterceptor());
    }
}
