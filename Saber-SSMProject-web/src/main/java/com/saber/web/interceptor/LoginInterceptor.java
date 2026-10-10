package com.saber.web.interceptor;

import com.saber.pojo.User;
import com.saber.web.exception.UserNoLoginException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("loginUser");
        if(user == null)
        {
            throw new UserNoLoginException("你还未登录 请登录");
        }
        return true;
    }
}
