package com.limingxu.shixun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.limingxu.shixun.entity.Tenement;
import com.limingxu.shixun.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT u.*, t.id AS tenement_id, t.name, t.link_name, t.phone_num, t.email " +
            "FROM t_user u " +
            "INNER JOIN t_tenement t ON u.tenement_id = t.id " +
            "WHERE u.tenement_id = #{tenementId} AND u.password = #{password}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "password", property = "password"),
            @Result(column = "department", property = "department"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "avatar", property = "avatar"),
            @Result(column = "tenement_id", property = "tenementId"),
            @Result(column = "tenement_id", property = "tenement.id"),
            @Result(column = "name", property = "tenement.name"),
            @Result(column = "link_name", property = "tenement.linkName"),
            @Result(column = "phone_num", property = "tenement.phoneNum"),
            @Result(column = "email", property = "tenement.email")
    })
    User findByTenementIdAndPassword(@Param("tenementId") Long tenementId, @Param("password") String password);
    @Insert("INSERT INTO t_user (name, password, department, create_time, avatar, tenement_id) " +
            "VALUES (#{name}, #{password}, #{department}, #{createTime}, #{avatar}, #{tenementId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertUser(User user);

}
