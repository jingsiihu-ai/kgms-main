package com.hjc.kgms.storage;

import com.hjc.kgms.config.StorageProperties;
import com.hjc.kgms.storage.impl.AliyunOssStorageService;
import com.hjc.kgms.storage.impl.LocalStorageService;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class StorageManager {

    private final StorageProperties storageProperties;
    private final LocalStorageService localStorageService;
    private final AliyunOssStorageService aliyunOssStorageService;

    public StorageManager(StorageProperties storageProperties,
                          LocalStorageService localStorageService,
                          AliyunOssStorageService aliyunOssStorageService) {
        this.storageProperties = storageProperties;
        this.localStorageService = localStorageService;
        this.aliyunOssStorageService = aliyunOssStorageService;
    }

    public StorageResult upload(String objectKey, InputStream inputStream, long size, String contentType) {
        if ("oss".equalsIgnoreCase(storageProperties.getProvider())) {
            try {
                return aliyunOssStorageService.upload(objectKey, inputStream, size, contentType);
            } catch (Exception ex) {
                return localStorageService.upload(objectKey, inputStream, size, contentType);
            }
        }
        return localStorageService.upload(objectKey, inputStream, size, contentType);
    }

    public InputStream download(String provider, String objectKey) {
        if ("OSS".equalsIgnoreCase(provider)) {
            return aliyunOssStorageService.download(objectKey);
        }
        return localStorageService.download(objectKey);
    }

    public void delete(String provider, String objectKey) {
        if ("OSS".equalsIgnoreCase(provider)) {
            aliyunOssStorageService.delete(objectKey);
            return;
        }
        localStorageService.delete(objectKey);
    }
}
