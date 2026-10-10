package com.saber.web.advice;

import com.saber.pojo.Message;
import com.saber.web.exception.ForbiddenException;
import com.saber.web.interceptor.AdminInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.saber.web.exception.UserNoLoginException;

@RestControllerAdvice
public class MyExceptionHandler
{
    private final Logger logger= LoggerFactory.getLogger(MyExceptionHandler.class);


    @ExceptionHandler(value = UserNoLoginException.class)
    public Message handleLoginException(UserNoLoginException e)
    {
        return new Message(401,e.getMessage(),null);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public Message handleForbiddenException(ForbiddenException e)
    {
        return new Message(403,e.getMessage(),null);
    }

    @ExceptionHandler(value=Exception.class)
    public Message handleException(Exception e)
    {
        logger.error("异常信息未处理",e.getClass().getName());
        return new Message(500,"服务器繁忙",null);
    }
}
