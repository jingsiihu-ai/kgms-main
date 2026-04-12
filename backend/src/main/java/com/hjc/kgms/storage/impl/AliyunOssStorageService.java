package com.hjc.kgms.storage.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.hjc.kgms.config.StorageProperties;
import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.storage.StorageResult;
import com.hjc.kgms.storage.StorageService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Component("aliyunOssStorageService")
public class AliyunOssStorageService implements StorageService {

    private final StorageProperties storageProperties;

    public AliyunOssStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    private OSS buildClient() {
        StorageProperties.Oss oss = storageProperties.getOss();
        if (oss == null || !StringUtils.hasText(oss.getEndpoint()) || !StringUtils.hasText(oss.getBucket())
                || !StringUtils.hasText(oss.getAccessKeyId()) || !StringUtils.hasText(oss.getAccessKeySecret())
                || oss.getAccessKeyId().contains("your-access-key-id")) {
            throw new BusinessException(500, "OSS 配置未完成，请先填写 application.yml 中 app.storage.oss 配置");
        }
        return new OSSClientBuilder().build(oss.getEndpoint(), oss.getAccessKeyId(), oss.getAccessKeySecret());
    }

    @Override
    public StorageResult upload(String objectKey, InputStream inputStream, long size, String contentType) {
        OSS client = buildClient();
        try {
            StorageProperties.Oss oss = storageProperties.getOss();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            if (StringUtils.hasText(contentType)) {
                metadata.setContentType(contentType);
            }
            client.putObject(oss.getBucket(), objectKey, inputStream, metadata);
            String baseUrl = oss.getBaseUrl();
            String url = StringUtils.hasText(baseUrl)
                    ? (baseUrl.endsWith("/") ? baseUrl + objectKey : baseUrl + "/" + objectKey)
                    : "https://" + oss.getBucket() + "." + oss.getEndpoint().replace("https://", "") + "/" + objectKey;
            return new StorageResult("OSS", objectKey, url);
        } catch (Exception ex) {
            throw new BusinessException(500, "OSS 上传失败: " + ex.getMessage());
        } finally {
            client.shutdown();
        }
    }

    @Override
    public InputStream download(String objectKey) {
        OSS client = buildClient();
        try {
            StorageProperties.Oss oss = storageProperties.getOss();
            OSSObject object = client.getObject(oss.getBucket(), objectKey);
            return object.getObjectContent();
        } catch (Exception ex) {
            client.shutdown();
            throw new BusinessException(404, "OSS 文件不存在或无法读取");
        }
    }

    @Override
    public void delete(String objectKey) {
        OSS client = null;
        try {
            client = buildClient();
            StorageProperties.Oss oss = storageProperties.getOss();
            client.deleteObject(oss.getBucket(), objectKey);
        } catch (Exception ignored) {
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }
}
