package com.hjc.kgms.util;

import com.hjc.kgms.enums.ResourceType;
import lombok.Data;

@Data
public class MetadataExtractResult {
    private ResourceType resourceType;
    private String mimeType;
    private String searchableText;
    private String metadataJson;
}
