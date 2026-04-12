package com.hjc.kgms.model.vo;

import lombok.Data;

@Data
public class UploadFailedItemVO {
    private String originalFilename;
    private String reason;
}
