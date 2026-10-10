package com.saber.service.impl;

import com.saber.dao.RoleDao;
import com.saber.pojo.Role;
import com.saber.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Primary
public class RoleServiceImpl implements RoleService
{
    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public List<Role> simplyFindAll() {
        return roleDao.simplyFindAll();
    }
}
