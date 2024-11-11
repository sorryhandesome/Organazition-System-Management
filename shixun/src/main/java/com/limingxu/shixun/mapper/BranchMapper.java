package com.limingxu.shixun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.limingxu.shixun.entity.Branch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface BranchMapper extends BaseMapper<Branch> {
    @Select("SELECT * FROM t_branch WHERE tenement_id = #{tenementId}")
    List<Branch> selectByTenementId(@Param("tenementId") Long tenementId);

}
