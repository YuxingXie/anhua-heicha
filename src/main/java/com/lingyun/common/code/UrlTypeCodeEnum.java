package com.lingyun.common.code;

/**
 * Url类型
 */
public enum UrlTypeCodeEnum {

    /**
     * 网页
     */
    PAGE(1),
    /**
     * 下载
     */
    DOWNLOAD(2);
    private int code;
    private UrlTypeCodeEnum(int code) {
        this.code=code;
    }
    public int toCode() {
        return this.code;
    }
    public static UrlTypeCodeEnum fromCode(int code) {
        for (UrlTypeCodeEnum wrongCodeEnum : UrlTypeCodeEnum.values()) {
            if (wrongCodeEnum.code==(code)) {
                return wrongCodeEnum;
            }
        }
        return null;
    }
}
