package com.limingxu.shixun.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limingxu.shixun.entity.Branch;
import com.limingxu.shixun.mapper.BranchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BranchBiz extends ServiceImpl<BranchMapper, Branch> {

    @Autowired
    private BranchMapper branchMapper;

    public List<Branch> getBranchesByTenementId(long tenementId) {
        List<Branch> branches = branchMapper.selectByTenementId(tenementId);
        List<Branch> result = new ArrayList<>();

        // 获取顶层部门
        for (Branch branch : branches) {
            if (branch.getParentId() == null) { // 顶层部门 parentId 为 null
                result.add(branch);
            }
        }

        // 递归处理每个顶层部门的子部门
        for (Branch top : result) {
            dealChildren(top, branches);
        }

        return result;
    }

    private void dealChildren(Branch branch, List<Branch> list) {
        for (Branch child : list) {
            if (branch.getBranchId().equals(child.getParentId())) { // 递归条件：子部门的 parentId 等于父部门的 branchId
                branch.getChildren().add(child);
                dealChildren(child, list); // 递归处理子部门
            }
        }
    }

    public void addBranch(Branch branch) {
        if (branch.getParentId() == null) {
            if (branch.getTenementId() == null) {
                throw new IllegalArgumentException("顶层部门必须设置 tenementId");
            }
        } else {
            if (branch.getTenementId() == null) {
                Branch parentBranch = branchMapper.selectById(branch.getParentId());
                if (parentBranch != null) {
                    branch.setTenementId(parentBranch.getTenementId());
                } else {
                    throw new IllegalArgumentException("无效的 parentId");
                }
            }
        }
        branchMapper.insert(branch);
    }


    public List<Branch> getBranchesByParentId(Integer parentId) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        return branchMapper.selectList(queryWrapper);
    }


    public void updateBranch(Integer branchId, Branch branch) {
        branch.setBranchId(branchId);
        branchMapper.updateById(branch);
    }

    public void deleteBranch(Integer branchId) {
        branchMapper.deleteById(branchId);
    }
}
