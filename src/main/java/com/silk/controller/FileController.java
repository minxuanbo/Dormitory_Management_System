package com.silk.controller;

import com.silk.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    // 上传根目录：与 MyWebMvcConfigurer 的 /uploads/** 静态映射保持同一位置（应用启动目录下的 uploads/）
    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads");

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String relativePath = datePath + "/" + fileName;

        try {
            // 一次性创建完整目录，避免漏建父级目录导致写入失败
            Path dir = UPLOAD_DIR.resolve(datePath);
            Files.createDirectories(dir);

            // 用流写入目标文件，规避 transferTo 在临时目录下的路径解析问题
            Path dest = dir.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }
            // 返回带上下文路径的完整可访问 URL，与 MyWebMvcConfigurer 的 /uploads/** 静态映射配套，
            // 避免前端 <img src="/uploads/..."> 被浏览器解析到域名根（http://host/uploads/...）导致 404
            return Result.ok("/dormitory/uploads/" + relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("上传失败");
        }
    }
}
