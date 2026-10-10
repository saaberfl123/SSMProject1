package com.saber.web.controller;

import com.saber.pojo.Message;
import com.saber.pojo.User;
import com.saber.pojo.constant.LoginCode;
import com.saber.pojo.constant.RegisterCode;
import com.saber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<Message> findAll()
    {
        List<User> users= userService.findAll();
        return ResponseEntity.ok().body(new Message(200,"查找成功",users));
    }

    @PostMapping("/register")
    public ResponseEntity<Message> register(User user)
    {
        Message message=new Message(RegisterCode.REGISTER_FAILED,"请输入注册信息!",null);
        HttpStatus status=HttpStatus.INTERNAL_SERVER_ERROR;
        if(user!=null)
        {
            int registerCode=userService.register(user);
            switch (registerCode)
            {
                case RegisterCode.REGISTER_SUCCESS:
                    message=new Message(registerCode,"注册成功",null);
                    status=HttpStatus.OK;
                    break;
                case RegisterCode.REGISTER_FAILED:
                    message=new Message(registerCode,"注册失败",null);
                    break;
                case RegisterCode.USER_EXIST:
                    message=new Message(registerCode,"用户名已存在",null);
                    status=HttpStatus.CONFLICT;
                    break;
            }
        }
        return ResponseEntity.status(status).body(message);
    }


    @PostMapping("/login")
    public ResponseEntity<Message> login(String username, String password, HttpSession session)
    {
        Message message=new Message(LoginCode.LOGIN_FILED,"请输入账号密码！",null);
        HttpStatus status=HttpStatus.INTERNAL_SERVER_ERROR;
        int loginCode=userService.login(username,password);
        switch (loginCode)
        {
            case LoginCode.LOGIN_SUCCESS:
                message=new Message(loginCode,"登录成功",null);
                User comtemporyUser=userService.findUserByUsername(username);
                comtemporyUser=userService.findUserWithRolesById(comtemporyUser.getId());
                comtemporyUser.setPassword(null);
                session.setAttribute("loginUser",comtemporyUser);
                status=HttpStatus.OK;
                break;
            case LoginCode.PASSWORD_WRONG:
                message=new Message(loginCode,"密码错误",null);
                status=HttpStatus.CONFLICT;
                break;
            case LoginCode.USERNAME_NOT_FOUND:
                message=new Message(loginCode,"用户不存在",null);
                status=HttpStatus.CONFLICT;
                break;
        }
        return ResponseEntity.status(status).body(message);
    }

    @GetMapping("/simplyFindAll")
    public ResponseEntity<Message> simplyFindAll()
    {
        return ResponseEntity.ok().body( new Message(200,"查找成功",userService.simplyFindAll()));
    }

    @GetMapping("/findPage")
    public ResponseEntity<Message> findPage(int pageNum, int pageSize)
    {
        return ResponseEntity.ok().body(new Message(200,"查找成功",userService.findPage(pageNum,pageSize)));
    }

}
