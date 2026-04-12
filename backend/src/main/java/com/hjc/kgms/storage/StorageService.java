package com.hjc.kgms.storage;

import java.io.InputStream;

public interface StorageService {
    StorageResult upload(String objectKey, InputStream inputStream, long size, String contentType);

    InputStream download(String objectKey);

    void delete(String objectKey);
}
