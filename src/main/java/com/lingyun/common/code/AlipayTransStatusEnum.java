package com.lingyun.common.code;

/**
 * Created by Administrator on 2016/10/15.
 * 已提交，已审核，已完成，失败
 */
public enum AlipayTransStatusEnum {
    SUBMITTED(1),PASSED(2),SUCCEED(3), FAILED(4);

    private int code;
    private AlipayTransStatusEnum(int code) {
        this.code=code;
    }
    public int toCode() {
        return this.code;
    }
    public static AlipayTransStatusEnum fromCode(int code) {
        for (AlipayTransStatusEnum alipayTransStatusEnum : AlipayTransStatusEnum.values()) {
            if (alipayTransStatusEnum.code==(code)) {
                return alipayTransStatusEnum;
            }
        }
        return null;
    }
}
