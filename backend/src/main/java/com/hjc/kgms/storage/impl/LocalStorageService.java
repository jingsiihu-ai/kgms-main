package com.hjc.kgms.storage.impl;

import com.hjc.kgms.config.StorageProperties;
import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.storage.StorageResult;
import com.hjc.kgms.storage.StorageService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component("localStorageService")
public class LocalStorageService implements StorageService {

    private final StorageProperties storageProperties;

    public LocalStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public StorageResult upload(String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            Path root = Paths.get(storageProperties.getLocalBasePath()).toAbsolutePath().normalize();
            Path target = root.resolve(objectKey).normalize();
            if (!target.startsWith(root)) {
                throw new BusinessException("非法文件路径");
            }
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target);
            String publicPath = storageProperties.getLocalPublicPath();
            if (publicPath == null || publicPath.isEmpty()) {
                publicPath = "/storage";
            }
            String url = publicPath + "/" + objectKey.replace("\\", "/");
            return new StorageResult("LOCAL", objectKey, url);
        } catch (IOException e) {
            throw new BusinessException(500, "本地文件存储失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream download(String objectKey) {
        try {
            Path root = Paths.get(storageProperties.getLocalBasePath()).toAbsolutePath().normalize();
            Path target = root.resolve(objectKey).normalize();
            if (!target.startsWith(root)) {
                throw new BusinessException("非法文件路径");
            }
            return Files.newInputStream(target);
        } catch (IOException e) {
            throw new BusinessException(404, "文件不存在或无法读取");
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            Path root = Paths.get(storageProperties.getLocalBasePath()).toAbsolutePath().normalize();
            Path target = root.resolve(objectKey).normalize();
            if (target.startsWith(root)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {
        }
    }
}
