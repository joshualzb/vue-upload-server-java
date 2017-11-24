package com.xc.fts.demo;

import javafx.application.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.MultipartConfigElement;

/**
 * Created by joshua on 2017/6/22.
 */
@Configuration
@ComponentScan(basePackageClasses = Application.class, useDefaultFilters = false)
public class ServletContextConfig extends WebMvcConfigurationSupport {

    @Value("${spring.redis.host}")
    private String SPRING_REDIS_HOST;

    @Value("${spring.redis.port}")
    private int SPRING_REDIS_PORT;

    @Bean
    public IdWorker curatorIdWorker() {
        return new IdWorker(1, 1);
    }

    /**
     * 文件上传配置
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("10240KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("102400KB");
        return factory.createMultipartConfig();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.
                addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "OPTIONS", "PUT")
                .maxAge(3600);
    }

    /**
     * 配置servlet处理
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
