package com.zerobase.everycampingbackend.common.fileutil;

import com.zerobase.everycampingbackend.domain.staticimage.type.ImageFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtils {

    public static String generateFilename(MultipartFile multipartFile) {
        String datetimeStr = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
        String uuid = datetimeStr + "-" + UUID.randomUUID();
        String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename())
            .replace(" ", "-");

        return uuid + "-" + fileName;
    }

    public static File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(multipartFile.getBytes());
        outputStream.close();
        return file;
    }

    public static boolean validateMultipartFile(MultipartFile multipartFile){
        if (ObjectUtils.isEmpty(multipartFile)) {
            return false;
        }

        int pos = Objects.requireNonNull(multipartFile.getOriginalFilename()).lastIndexOf(".");
        String extension = multipartFile.getOriginalFilename().substring(pos + 1);
        return !ObjectUtils.isEmpty(ImageFormat.getExtension(extension));
    }

    public static MultipartFile resizeImage(MultipartFile multipartFile){
        return null;
    }
}
