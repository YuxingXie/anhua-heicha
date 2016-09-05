package com.lingyun.common.web;

import java.util.List;

public class Pagination<T> {

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 查询到的总数据量
     */
    private Integer totalNumber = 0;

    /**
     * 数据集
     */
    private List items;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

//    public void setTotalPage(Integer totalPage) {
//        this.totalPage = totalPage;
//    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public Pagination(Integer pageSize, Integer currentPage, List items) {
        if (pageSize.intValue()==0) throw new RuntimeException("page size must not be zero");
        if (currentPage.intValue()<=0) throw new RuntimeException("current page must bigger than zero");
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.items = items;
        this.totalNumber=items==null?0:items.size();
        int mod=this.totalNumber%this.pageSize;
        this.totalPage=mod==0?this.totalNumber/this.pageSize:this.totalNumber/this.pageSize+1;
    }
}