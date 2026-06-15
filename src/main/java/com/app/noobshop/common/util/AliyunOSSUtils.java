package com.app.noobshop.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.app.noobshop.properties.AliyunOSSProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class AliyunOSSUtils {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String DEFAULT_IMAGE_DIR = "image";

    @Resource
    private AliyunOSSProperties aliyunOSSProperties;

    public String upload(MultipartFile file) {
        return upload(file, DEFAULT_IMAGE_DIR);
    }

    public List<String> uploadBatch(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("files can not be empty");
        }
        List<String> urls = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            urls.add(upload(file));
        }
        return urls;
    }

    public String upload(MultipartFile file, String bizDir) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file can not be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = buildObjectName(bizDir, extension);

        OSS ossClient = new OSSClientBuilder().build(
                aliyunOSSProperties.getEndpoint(),
                aliyunOSSProperties.getAccessKeyId(),
                aliyunOSSProperties.getAccessKeySecret()
        );

        try {
            ossClient.putObject(aliyunOSSProperties.getBucketName(), fileName, file.getInputStream());
        } catch (IOException e) {
            log.error("upload file to aliyun oss failed: {}", e.getMessage());
            throw new RuntimeException("file upload failed");
        } finally {
            ossClient.shutdown();
        }

        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(aliyunOSSProperties.getBucketName())
                .append(".")
                .append(aliyunOSSProperties.getEndpoint())
                .append("/")
                .append(fileName);

        log.info("upload file to aliyun oss success, url: {}", stringBuilder);
        return stringBuilder.toString();
    }

    private String buildObjectName(String bizDir, String extension) {
        String dir = (bizDir == null || bizDir.isBlank()) ? DEFAULT_IMAGE_DIR : bizDir;
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        return dir + "/" + datePath + "/" + UUID.randomUUID() + extension;
    }
}
