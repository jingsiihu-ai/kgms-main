package com.hjc.kgms.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageResult {
    private String provider;
    private String objectKey;
    private String url;
}
