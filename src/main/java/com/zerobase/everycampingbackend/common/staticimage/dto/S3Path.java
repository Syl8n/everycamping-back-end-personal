package com.zerobase.everycampingbackend.common.staticimage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3Path {
    private String imageUri;
    private String imagePath;
}
