package com.education.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    // 上传接口 (保持不变)
    @PostMapping("/file")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("文件为空");
        try {
            String projectPath = System.getProperty("user.dir");
            File uploadDir = new File(projectPath + "/uploads");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            File dest = new File(uploadDir, newFileName);
            file.transferTo(dest);

            return "/uploads/" + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败");
        }
    }

    // 👇👇👇 新增：强制下载接口 👇👇👇
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String fileName) {
        try {
            // 1. 定位文件路径
            Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads/" + fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // 2. 设置强制下载的响应头 (Content-Disposition: attachment)
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("文件不存在");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("下载出错: " + e.getMessage());
        }
    }
}