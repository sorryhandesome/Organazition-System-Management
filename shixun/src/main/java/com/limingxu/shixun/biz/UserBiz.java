package com.limingxu.shixun.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.limingxu.shixun.entity.Tenement;
import com.limingxu.shixun.entity.User;
import com.limingxu.shixun.mapper.TenementMapper;
import com.limingxu.shixun.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBiz {
    private TenementBiz tenementBiz;
    private UserMapper userMapper;

    @Transactional
    public boolean registerUser(User user) {
        int i=userMapper.insertUser(user);
        return i>0;
    }
    public boolean updateUser(User user) {
        int userUpdateCount = userMapper.updateById(user);
        if (user.getTenement() != null) {
            tenementBiz.updateTenement(user.getTenement());
        }
        return userUpdateCount > 0;
    }
    public User loginByTenementNameAndPassword(long id, String password) {
        return userMapper.findByTenementIdAndPassword(id, password);
    }
    public UserBiz(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user != null && user.getPassword().equals(currentPassword)) {
            user.setPassword(newPassword);
            int result = userMapper.updateById(user);
            return result > 0;
        }
        return false;
    }
}
