package com.saber.dao;

import com.saber.pojo.Role;
import com.saber.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao
{
    @Select("SELECT * from users where id in(select userId from user_role WHERE roleId=#{id})")
    public List<User> findUserByRoleId(int id);

    @Select("select * from users")
    public List<User> simplyFindAll();


    @Results(id = "user", value = {
            @Result(id =true,column = "id",property = "id"),
            @Result(column = "username",property = "username"),
            @Result(column = "email",property = "email"),
            @Result(column = "password",property = "password"),
            @Result(column = "phoneNum",property = "phoneNum"),
            @Result(column = "status",property = "status"),
            @Result(column = "id",property = "roles",many = @Many(select = "com.saber.dao.RoleDao.findRolesByUserId")),
    })
    @Select("select * from users")
    public List<User> findAll();

    @Insert("INSERT INTO users(username, email, password, phoneNum, status) VALUES(#{username}, #{email}, #{password}, #{phoneNum}, #{status})")
    public void save(User user);

    @Select("select * from users where id=#{id}")
    public User findUserById(int id);

    @Select("select * from users where username=#{username}")
    public User findUserByUsername(String username);

    @Results(id = "userWithRoles", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "email", property = "email"),
            @Result(column = "password", property = "password"),
            @Result(column = "phoneNum", property = "phoneNum"),
            @Result(column = "status", property = "status"),
            @Result(column = "id", property = "roles", many = @Many(select = "com.saber.dao.RoleDao.findRolesByUserId")),
    })
    @Select("select * from users where id=#{id}")
    public User findUserWithRolesById(int id);




}
