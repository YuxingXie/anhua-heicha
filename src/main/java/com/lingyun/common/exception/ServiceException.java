package com.lingyun.common.exception;

/**
 * Created by Administrator on 2015/7/6.
 */
public class ServiceException extends Exception {
    private static final long serialVersionUID = -1708015121235851228L;

    public ServiceException(String message) {
        super(message);
    }
}