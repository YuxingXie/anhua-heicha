package com.lingyun.common.code;

/**
 * 1:见点奖，2:对碰奖，3：直推奖，4：小区
 */
public enum UserMeasureSortEnum {
    JIANDIAN(1), DUIPENG(2),ZHITUI(3),XIAOQU(4);
    private int code;
    private UserMeasureSortEnum(int code) {
        this.code=code;
    }
    public int toCode() {
        return this.code;
    }
    public static UserMeasureSortEnum fromCode(int code) {
        for (UserMeasureSortEnum userMeasureSortEnum : UserMeasureSortEnum.values()) {
            if (userMeasureSortEnum.code==(code)) {
                return userMeasureSortEnum;
            }
        }
        return null;
    }
}
