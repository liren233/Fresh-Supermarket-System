package com.xhm.main.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir")
            + "/../vue-shop/shop-front/public/static/images/goods/";

    @PostMapping("/fileUpload")
    public Map<String, Object> fileUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("code", 1);
            result.put("msg", "文件不能为空");
            return result;
        }
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;
        try {
            file.transferTo(new File(dir, newFileName));
            String visitPath = "goods/" + newFileName;
            result.put("code", 0);
            result.put("msg", "上传成功");
            result.put("src", visitPath);
        } catch (IOException e) {
            result.put("code", 1);
            result.put("msg", "上传失败：" + e.getMessage());
        }
        return result;
    }
}