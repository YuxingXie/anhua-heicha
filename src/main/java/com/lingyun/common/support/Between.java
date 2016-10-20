package com.lingyun.common.support;

/**
 * Created by Administrator on 2016/10/20.
 */
public class Between<T extends Comparable> {
    private T begin;
    private T end;

    public T getBegin() {
        return begin;
    }

    public void setBegin(T begin) {
        this.begin = begin;
    }

    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }
}
