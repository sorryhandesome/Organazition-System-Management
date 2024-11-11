package com.limingxu.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_branch")
public class Branch {
    @TableId( type = IdType.AUTO)
    private Integer branchId;

    @TableField("parent_id")
    private Integer parentId;

    @TableField("name")
    private String name;

    @TableField("num")
    private String num;

    @TableField("state")
    private boolean state;

    @TableField("create_time")
    private String createTime;

    @TableField("link_name")
    private String linkName;

    @TableField("phone_num")
    private String phoneNum;
    @TableField("email")
    private String email;

    @TableField("tenement_id")
    private Integer tenementId;

    @TableField(exist = false)
    private List<Branch> children=new ArrayList();

    @TableField(exist = false)
    private boolean hasChildren;
}
