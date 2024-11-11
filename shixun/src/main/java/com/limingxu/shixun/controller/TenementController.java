package com.limingxu.shixun.controller;

import com.limingxu.shixun.biz.TenementBiz;
import com.limingxu.shixun.entity.Tenement;
import com.limingxu.shixun.entity.User;
import com.limingxu.shixun.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/tenement")
public class TenementController {

    @Autowired
    private TenementBiz tenementBiz;

    @PostMapping("/regist")
    public Result<?> regist(@RequestBody Tenement tenement) {
        try {
            boolean isNameAvailable = tenementBiz.isNameAvailable(tenement.getName());
            if (isNameAvailable) {
                return Result.error("1", "租户名已存在");
            }
            boolean isSuccess = tenementBiz.register(tenement);
            if (isSuccess) {
                System.out.println(tenement.getId());
                return Result.success(tenement.getId());
            } else {
                return Result.error("2", "注册失败");
            }
        } catch (Exception e) {
            return Result.error("3", "注册失败：" + e.getMessage());
        }
    }
}
