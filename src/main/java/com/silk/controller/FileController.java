package com.silk.controller;

import com.silk.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePath = sdf.format(new Date());

        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String relativePath = datePath + "/" + fileName;

        File dir = new File(UPLOAD_DIR + datePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(UPLOAD_DIR + relativePath);
        try {
            file.transferTo(dest);
            return Result.ok("/uploads/" + relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("上传失败");
        }
    }
}
