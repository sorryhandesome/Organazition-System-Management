package com.limingxu.shixun.controller;

import com.limingxu.shixun.biz.AdminBiz;
import com.limingxu.shixun.entity.Admin;
import com.limingxu.shixun.util.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminBiz adminBiz;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Admin admin, HttpSession session) {
        try {
            Admin loginAdmin = adminBiz.adminLogin(admin.getName(), admin.getPassword());
            if (loginAdmin != null) {
                session.setAttribute("Identity", "admin");
                session.setAttribute("user", loginAdmin);
                return Result.success(loginAdmin);
            } else {
                return Result.error("1", "用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("3", "登录失败：" + e.getMessage());
        }
    }

    @PutMapping("/changePassword")
    public Result<?> changePassword(@RequestBody Map<String, String> request) {
        Long adminId = Long.valueOf(request.get("userId")); // 注意：这里的 userId 是 adminId
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        String confirmNewPassword = request.get("confirmNewPassword");

        if (!newPassword.equals(confirmNewPassword)) {
            return Result.error("-1", "新密码和确认密码不一致");
        }

        boolean success = adminBiz.changePassword(adminId, currentPassword, newPassword);
        if (success) {
            return Result.success();
        } else {
            return Result.error("-1", "密码修改失败");
        }
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Admin admin) {
        int i = adminBiz.updateAdmin(admin);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    public void setAdminBiz(AdminBiz adminBiz) {
        this.adminBiz = adminBiz;
    }
}
