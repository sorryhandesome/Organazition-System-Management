package com.limingxu.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("name")
    private String name;
    @TableField("password")
    private String password;
    @TableField("department")
    private String department;
    @TableField("create_time")
    private Date createTime;
    @TableField("avatar")
    private String avatar;
    @TableField("tenement_id")
    private Long tenementId;


    @TableField(exist = false)
    private Tenement tenement;
}
