package com.zzx.exam.vo;

import com.qcloud.cos.model.PutObjectResult;
import lombok.Data;

@Data
public class PutObjectResultVO {

    private String fileUrl;
    private PutObjectResult putObjectResult;
}
