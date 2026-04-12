package com.hjc.kgms.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UploadBatchResultVO {
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private List<ResourceVO> successList = new ArrayList<>();
    private List<UploadFailedItemVO> failedList = new ArrayList<>();
}
