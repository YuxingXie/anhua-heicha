package com.lingyun.support.vo;

/**
 * Created by Administrator on 2015/12/23.
 */
public class Sortable {
    private String field;
    private boolean asc;

    public void setField(String field) {
        this.field = field;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public String getField() {
        return field;
    }

    public boolean getAsc() {
        return asc;
    }
}
