package com.hjc.kgms.service;

import com.hjc.kgms.util.MetadataExtractResult;
import org.springframework.web.multipart.MultipartFile;

public interface MetadataExtractService {
    MetadataExtractResult extract(MultipartFile file);
}
