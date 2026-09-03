package com.app.noobshop.controller.tool;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.common.util.AliyunOSSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.app.noobshop.security.constant.SecurityExpressions.FILE_UPLOAD;

@RestController
@RequestMapping("/api")
@Tag(name = "工具")
@Slf4j
public class ToolController {

    @Resource
    private AliyunOSSUtils aliyunOSSUtils;

    @PostMapping("/upload/image")
    @PreAuthorize(FILE_UPLOAD)
    @Operation(summary = "图片上传")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("图片上传: {}", file.getOriginalFilename());
        String url = aliyunOSSUtils.upload(file);
        return Result.success(url);
    }

    @PostMapping("/upload/images")
    @PreAuthorize(FILE_UPLOAD)
    @Operation(summary = "图片批量上传")
    public Result<List<String>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        log.info("图片批量上传: {}", files == null ? 0 : files.length);
        List<String> urls = aliyunOSSUtils.uploadBatch(files);
        return Result.success(urls);
    }
}
