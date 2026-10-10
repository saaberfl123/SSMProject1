package com.saber.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.saber.dao.UserDao;
import com.saber.pojo.User;
import com.saber.pojo.constant.LoginCode;
import com.saber.pojo.constant.RegisterCode;
import com.saber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService
{
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public List<User> simplyFindAll() {
        return userDao.simplyFindAll();
    }

    @Override
    public PageInfo<User> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userDao.simplyFindAll();
        return new PageInfo<>(users);
    }

    @Override
    public User findById(int id) {
        return userDao.findUserById(id);
    }

    @Override
    public int register(User user) {
        int registerCode=RegisterCode.REGISTER_FAILED;
        if(user!=null)
        {
            User NameUser=userDao.findUserByUsername(user.getUsername());
            if(NameUser==null)
            {
                userDao.save(user);
                registerCode=RegisterCode.REGISTER_SUCCESS;
            }
            else
            {
                registerCode=RegisterCode.USER_EXIST;
            }
        }
        return registerCode;
    }

    @Override
    public int login(String username, String password) {
        User user=userDao.findUserByUsername(username);
        if(user==null) return LoginCode.USERNAME_NOT_FOUND;
        else if(user.getPassword().equals(password))return LoginCode.LOGIN_SUCCESS;
        else return LoginCode.PASSWORD_WRONG;
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public User findUserWithRolesById(int id) {
        return userDao.findUserWithRolesById(id);
    }


}
