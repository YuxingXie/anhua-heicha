package com.lingyun.entity;

/**
 * 产品宣传册.
 */
public class ProductBrochures {
    private String type;//"img":图片,"page":网页
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
