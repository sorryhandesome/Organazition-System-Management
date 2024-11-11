package com.limingxu.shixun.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.limingxu.shixun.entity.Admin;
import com.limingxu.shixun.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBiz {
    @Autowired
    private AdminMapper adminMapper;
    public void setAdminMapper(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }
    public Admin adminLogin(String name, String password) {
        QueryWrapper<Admin> qw = new QueryWrapper<>();
        qw.eq("name", name);
        qw.eq("password", password);
        Admin admin = adminMapper.selectOne(qw);
        if (admin != null) {
            return admin;
        } else {
            return null;
        }
    }
    public int updateAdmin(Admin admin) {
        int i = adminMapper.updateById(admin);
        return i;
    }
    public boolean changePassword(Long adminId, String currentPassword, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin != null && admin.getPassword().equals(currentPassword)) {
            admin.setPassword(newPassword);
            int result = adminMapper.updateById(admin);
            return result > 0;
        }
        return false;
    }
}

