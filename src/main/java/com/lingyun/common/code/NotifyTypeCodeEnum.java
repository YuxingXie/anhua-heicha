package com.lingyun.common.code;

/**
 * 通知类型
 */
public enum NotifyTypeCodeEnum {

    /**
     * 系统消息
     */
    SYSTEM("SYSTEM"),
    ADMIN("ADMIN");
    private String code;
    private NotifyTypeCodeEnum(String code) {
        this.code=code;
    }
    public String toCode() {
        return this.code;
    }
    public static NotifyTypeCodeEnum fromCode(String code) {
        for (NotifyTypeCodeEnum notifyTypeCodeEnum : NotifyTypeCodeEnum.values()) {
            if (notifyTypeCodeEnum.code.equals(code)) {
                return notifyTypeCodeEnum;
            }
        }
        return null;
    }
}
