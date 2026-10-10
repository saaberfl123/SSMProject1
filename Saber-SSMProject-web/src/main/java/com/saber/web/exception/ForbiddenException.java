package com.saber.web.exception;

/**
 * 用户不足权限访问异常
 */
public class ForbiddenException extends RuntimeException
{
    public ForbiddenException(String message) {
        super(message);
    }
}
