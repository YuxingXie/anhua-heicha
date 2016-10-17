package com.lingyun.common.code;

/**
 * //1信用卡，2储蓄卡，3支付宝
 */
public enum CardSortEnum {
    CREDIT_CARD("1"), DEBIT_CARD("2"),ALIPAY("3");
//    ,XIAOQU(4);
//    ,XIAOQU_YEWUZHUREN(4);
//    ,XIAOQU_YEWUJINGLI(5)
//    ,XIAOQU_GAOJIYEWUJINGLI(6)
//    ,XIAOQU_DONGSHI(7)
//    ,XIAOQU_YINDONG(8)
//    ,XIAOQU_JINDONG(9);
    private String code;
    private CardSortEnum(String code) {
        this.code=code;
    }
    public String toCode() {
        return this.code;
    }
    public static CardSortEnum fromCode(int code) {
        for (CardSortEnum userMeasureSortEnum : CardSortEnum.values()) {
            if (userMeasureSortEnum.code.equals(code)) {
                return userMeasureSortEnum;
            }
        }
        return null;
    }
}
