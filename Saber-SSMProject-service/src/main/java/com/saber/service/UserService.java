package com.saber.service;

import com.github.pagehelper.PageInfo;
import com.saber.pojo.User;

import java.util.List;

public interface UserService
{
    public List<User> findAll();

    public List<User> simplyFindAll();

    public PageInfo<User> findPage(int pageNum, int pageSize);

    public User findById(int id);

    public int register(User user);

    public int login(String username, String password);

    User findUserByUsername(String username);

    public User findUserWithRolesById(int id);
}
