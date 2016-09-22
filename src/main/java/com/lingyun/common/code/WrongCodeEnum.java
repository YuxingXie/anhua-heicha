package com.lingyun.common.code;

/**
 * 错误代码
 */
public enum WrongCodeEnum {

    /**
     * 未登录
     */
    NOT_LOGIN(1),
    /**
     * 已登录
     */
    ALREADY_LOGIN(2),
    /**
     * 无权限
     */
    NO_PERMISSION(3);
    private int code;
    private WrongCodeEnum(int code) {
        this.code=code;
    }
    public int toCode() {
        return this.code;
    }
    public static WrongCodeEnum fromCode(int code) {
        for (WrongCodeEnum wrongCodeEnum : WrongCodeEnum.values()) {
            if (wrongCodeEnum.code==(code)) {
                return wrongCodeEnum;
            }
        }
        return null;
    }
}
