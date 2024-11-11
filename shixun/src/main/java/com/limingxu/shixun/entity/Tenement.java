package com.limingxu.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_tenement")
public class Tenement {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("link_name")
    private String linkName;

    @TableField("num")
    private int num = 0;

    @TableField("phone_num")
    private String phoneNum;

    @TableField("state")
    private boolean state = true;

    @TableField("create_time")
    private String createTime;

    @TableField("email")
    private String email;

    @TableField(exist = false)
    private List<Branch> children;

    @TableField(exist = false)
    private boolean hasChildren;
}
