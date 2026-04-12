package com.hjc.kgms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    public WebMvcConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String mapping = storageProperties.getLocalPublicPath();
        if (mapping == null || mapping.isEmpty()) {
            mapping = "/storage";
        }
        if (!mapping.endsWith("/")) {
            mapping = mapping + "/";
        }
        String location = Paths.get(storageProperties.getLocalBasePath()).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler(mapping + "**")
                .addResourceLocations(location);
    }
}
