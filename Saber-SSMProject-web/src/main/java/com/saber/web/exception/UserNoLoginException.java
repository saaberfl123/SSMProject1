package com.saber.web.exception;

/**
 * 用户未登录异常
 */
public class UserNoLoginException extends RuntimeException
{
    public UserNoLoginException(String message) {
        super(message);
    }
}
