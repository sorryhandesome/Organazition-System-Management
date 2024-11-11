package com.limingxu.shixun.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.limingxu.shixun.entity.Branch;
import com.limingxu.shixun.entity.Tenement;
import com.limingxu.shixun.mapper.TenementMapper;
import com.limingxu.shixun.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class TenementBiz {
    @Autowired
    private TenementMapper tenementMapper;

    @Transactional
    public boolean register(Tenement tenement) {
        int result = tenementMapper.insert(tenement);
        return result > 0;
    }
    public List<Tenement> getAllTenements(QueryWrapper<Tenement> wrapper) {
        return tenementMapper.selectList(wrapper);
    }
    public boolean isNameAvailable(String name) {
        QueryWrapper<Tenement> qw = new QueryWrapper<>();
        qw.eq("name", name);
        return tenementMapper.selectCount(qw) > 0;
    }
    public Tenement getTenementByName(String name) {
        QueryWrapper<Tenement> qw = new QueryWrapper<>();
        qw.eq("name", name);
        Tenement tenement= tenementMapper.selectOne(qw);
        if(tenement!=null){
            return tenement;
        }else{
            return null;
        }
    }

    public void setTenementMapper(TenementMapper tenementMapper) {
        this.tenementMapper = tenementMapper;
    }

    public boolean updateTenement(Tenement tenement) {
        int updateCount = tenementMapper.updateById(tenement);
        return updateCount > 0;
    }
    public boolean updateById(Tenement tenement) {
        int updatedRows = tenementMapper.updateById(tenement);
        return updatedRows > 0;
    }
    public Tenement getById(Long id) {
        return tenementMapper.selectById(id);

    }
}
