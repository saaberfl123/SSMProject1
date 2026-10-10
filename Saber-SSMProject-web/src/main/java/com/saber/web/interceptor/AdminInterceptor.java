package com.saber.web.interceptor;

import com.saber.pojo.Role;
import com.saber.pojo.User;
import com.saber.web.exception.ForbiddenException;
import com.saber.web.exception.UserNoLoginException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class AdminInterceptor extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("loginUser");
        if(user != null)
        {
            List<Role> roles = user.getRoles();
            if(roles!=null&&!roles.isEmpty())
            {
                for (Role role : roles) {
                    if (role.getRoleName().equals("admin"))
                        return true;
                }
                throw new ForbiddenException("你的权限不足！");
            }
        }
        throw new LoginException("你还未登录！");//不能return false的原因 因为要保持统一的异常管理
    }
}
