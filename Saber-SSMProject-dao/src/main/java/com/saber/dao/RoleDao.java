package com.saber.dao;

import com.saber.pojo.Role;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleDao
{
    @Results(id = "role", value = {
            @Result(id =true,column = "id",property = "id"),
            @Result(column = "roleName",property = "roleName"),
            @Result(column = "roleDesc",property = "roleDesc"),
            @Result(column = "id",property = "users",many=@Many(select = "com.saber.dao.UserDao.findUserByRoleId"))
    })
    @Select("select * from roles")
    public List<Role> findAll();

    @Select("SELECT * from roles where id in(select roleId from user_role WHERE userId=#{id})")
    public List<Role> findRolesByUserId(int id);

    @Select("select * from roles")
    public List<Role> simplyFindAll();


}
