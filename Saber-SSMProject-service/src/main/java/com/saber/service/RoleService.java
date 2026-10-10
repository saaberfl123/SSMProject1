package com.saber.service;

import com.saber.pojo.Role;

import java.util.List;

public interface RoleService
{
    public List<Role> findAll();

    public List<Role> simplyFindAll();
}
