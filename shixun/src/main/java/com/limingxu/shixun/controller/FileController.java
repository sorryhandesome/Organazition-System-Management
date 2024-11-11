package com.limingxu.shixun.controller;


import com.limingxu.shixun.biz.AdminBiz;
import com.limingxu.shixun.biz.TenementBiz;
import com.limingxu.shixun.biz.UserBiz;
import com.limingxu.shixun.entity.User;
import com.limingxu.shixun.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.limingxu.shixun.entity.Admin;
import com.limingxu.shixun.entity.Tenement;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {
    private static String originalFilename = "";
    private final ApplicationHome applicationHome = new ApplicationHome(this.getClass());
    private final String path = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\image\\";

    @Autowired
    private AdminBiz adminBiz;

    @Autowired
    private TenementBiz tenementBiz;

    @Autowired
    private UserBiz userBiz;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("1", "上传失败，文件为空");
        }

        // 获取文件名
        originalFilename = file.getOriginalFilename();
        String ext = originalFilename.split("\\.")[1];
        String uid = UUID.randomUUID().toString().replace("-", "");
        originalFilename = uid + "." + ext;

        // 存储位置
        String targetPath = path + originalFilename;
        System.out.println("文件保存路径：" + targetPath);
        try {
            // 保存文件
            file.transferTo(new File(targetPath));
            return Result.success(originalFilename);
        } catch (IOException e) {
            return Result.error("2", "上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/initAvatar/{filename}")
    public Result<?> initAvatar(@PathVariable String filename) throws IOException {
        System.out.println(filename);
        String path1 = path + filename;
        System.out.println(path1);
        return Result.success(getImage(path1));
    }

    private Result<?> getImage(String path) throws IOException {
        // 读取图片变成字节数组
        FileInputStream fileInputStream = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = fileInputStream.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        byte[] fileByte = bos.toByteArray();

        // 进行 base64 编码
        Base64.Encoder encoder = Base64.getEncoder();
        String data = encoder.encodeToString(fileByte);

        fileInputStream.close();
        bos.close();
        return Result.success(data);
    }

    public void setAdminBiz(AdminBiz adminBiz) {
        this.adminBiz = adminBiz;
    }

    public void setTenementBiz(TenementBiz tenementBiz) {
        this.tenementBiz = tenementBiz;
    }

    public void setUserBiz(UserBiz userBiz){
        this.userBiz=userBiz;
    }
}
