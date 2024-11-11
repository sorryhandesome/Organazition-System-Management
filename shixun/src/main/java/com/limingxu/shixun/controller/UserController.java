package com.limingxu.shixun.controller;

import com.limingxu.shixun.biz.TenementBiz;
import com.limingxu.shixun.biz.UserBiz;
import com.limingxu.shixun.entity.Tenement;
import com.limingxu.shixun.entity.User;
import com.limingxu.shixun.util.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private TenementBiz tenementBiz;

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user, HttpSession session) {
        try {
            Tenement tenement = tenementBiz.getTenementByName(user.getName());
            User loginUser = userBiz.loginByTenementNameAndPassword(tenement.getId(), user.getPassword());
            if (loginUser != null) {
                loginUser.setTenement(tenement); // 确保登录用户对象中包含Tenement信息
                session.setAttribute("Identity", "user");
                session.setAttribute("user", loginUser);
                return Result.success(loginUser);
            } else {
                return Result.error("1", "租户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("3", "登录失败：" + e.getMessage());
        }
    }

    @PutMapping("/changePassword")
    public Result<?> changePassword(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(request.get("userId"));
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        String confirmNewPassword = request.get("confirmNewPassword");
        if (!newPassword.equals(confirmNewPassword)) {
            return Result.error("-1", "新密码和确认密码不一致");
        }

        boolean success = userBiz.changePassword(userId, currentPassword, newPassword);
        if (success) {
            return Result.success();
        } else {
            return Result.error("-1", "密码修改失败");
        }
    }
    @PutMapping("/update")
    public Result<?> update(@RequestBody User user) {
        boolean success = userBiz.updateUser(user);
        if (success) {
            return Result.success();
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            boolean isSuccess = userBiz.registerUser(user);
            if (isSuccess) {
                return Result.success("用户注册成功");
            } else {
                return Result.error("2", "用户注册失败");
            }
        } catch (Exception e) {
            return Result.error("3", "用户注册失败：" + e.getMessage());
        }
    }

    public void setUserBiz(UserBiz userBiz) {
        this.userBiz = userBiz;
    }

    public void setTenementBiz(TenementBiz tenementBiz) {
        this.tenementBiz = tenementBiz;
    }
}
