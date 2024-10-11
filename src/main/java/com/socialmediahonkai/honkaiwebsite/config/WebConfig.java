package com.socialmediahonkai.honkaiwebsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Necessary for uploading files to API.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Mapping an external URI path to be uses as resource by Spring Boot.
        // https://www.baeldung.com/spring-mvc-static-resources
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
