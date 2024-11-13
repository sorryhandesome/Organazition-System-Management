package com.limingxu.shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.limingxu.shixun.entity.Branch;
import com.limingxu.shixun.entity.Tenement;
import com.limingxu.shixun.biz.BranchBiz;
import com.limingxu.shixun.biz.TenementBiz;
import com.limingxu.shixun.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branch")
public class BranchController {

    @Autowired
    private BranchBiz branchBiz;

    @Autowired
    private TenementBiz tenementBiz;

    @GetMapping("/tenements")
    public Result<?> getTopTenements(@RequestParam(value = "search", required = false) String search) {
        QueryWrapper<Tenement> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            queryWrapper.like("name", search);
        }
        List<Tenement> list = tenementBiz.getAllTenements(queryWrapper);
        for (Tenement tenement : list) {
            tenement.setHasChildren(true);
        }
        return Result.success(list);
    }

    @GetMapping("/allTenementsWithBranches")
    public Result<List<Tenement>> getAllTenementsWithBranches() {
        List<Tenement> tenements = tenementBiz.getAllTenements(null);
        tenements.forEach(tenement -> {
            List<Branch> branches = branchBiz.getBranchesByTenementId(tenement.getId());
            tenement.setChildren(branches);
        });
        return Result.success(tenements);
    }

    @GetMapping("/tenantsBranches")
    public Result<Tenement> getTenantsBranches(@RequestParam long tenementId) {
        Tenement tenement = tenementBiz.getById(tenementId);
        if (tenement == null) {
            return Result.error("1", "Tenement not found");
        }
        List<Branch> branches = branchBiz.getBranchesByTenementId(tenementId);
        tenement.setChildren(branches);
        return Result.success(tenement);
    }

    @GetMapping("/children")
    public Result<List<Branch>> getChildrenBranches(@RequestParam Integer parentId) {
        List<Branch> branches = branchBiz.getBranchesByParentId(parentId);
        branches.forEach(branch -> branch.setHasChildren(true));
        return Result.success(branches);
    }


    @PostMapping("/add")
    public Result<?> addBranch(@RequestBody Branch branch) {
        try {
            branchBiz.addBranch(branch);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error("1",e.getMessage());
        }
    }


    @GetMapping("/tenement/{id}")
    public Result<?> getTenementById(@PathVariable Long id) {
        Tenement tenement = tenementBiz.getById(id);
        if (tenement == null) {
            return Result.error("1","Tenement not found");
        }
        return Result.success(tenement);
    }

    // 获取下属部门的数据
    @GetMapping("/{branchId}")
    public Result<?> getBranchById(@PathVariable Integer branchId) {
        Branch branch = branchBiz.getById(branchId);
        if (branch == null) {
            return Result.error("1","Branch not found");
        }
        return Result.success(branch);
    }


    @DeleteMapping("/{branchId}")
    public Result<?> deleteBranch(@PathVariable Integer branchId) {
        branchBiz.deleteBranch(branchId);
        return Result.success();
    }
    @PutMapping("/{branchId}")
    public Result<?> updateBranch(@PathVariable Integer branchId, @RequestBody Branch branch) {
        System.out.println("Update branch: " + branchId + ", " + branch);
        branchBiz.updateBranch(branchId, branch);
        return Result.success();
    }
    @PutMapping("/tenement/{id}")
    public Result<?> updateTenement(@PathVariable Long id, @RequestBody Tenement tenement) {
        tenement.setId(id);
        tenementBiz.updateById(tenement);
        return Result.success();
    }
}
