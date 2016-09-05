package com.lingyun.entity;

import javax.validation.constraints.NotNull;
import java.util.Date;


public class ProductSeriesPrice {

    @NotNull
    private Double price;
    @NotNull
    private Date beginDate;
    private Date endDate;
    @NotNull
    private Date adjustDate;
    private String comment;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getAdjustDate() {
        return adjustDate;
    }

    public void setAdjustDate(Date adjustDate) {
        this.adjustDate = adjustDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public ProductSeries getProductSeries() {
//        return productSeries;
//    }
//
//    public void setProductSeries(ProductSeries productSeries) {
//        this.productSeries = productSeries;
//    }

//    public ProductSeriesPrice getPrevPrice() {
//        return prevPrice;
//    }
//
//    public void setPrevPrice(ProductSeriesPrice prevPrice) {
//        this.prevPrice = prevPrice;
//    }
}