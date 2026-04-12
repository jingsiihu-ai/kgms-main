package com.hjc.kgms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {
    private String provider;
    private String localBasePath;
    private String localPublicPath;
    private Oss oss = new Oss();

    @Data
    public static class Oss {
        private String endpoint;
        private String bucket;
        private String accessKeyId;
        private String accessKeySecret;
        private String baseUrl;
    }
}
