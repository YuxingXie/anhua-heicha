package com.lingyun.entity;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 这不是一个持久化document，是一个持久化中间类(作为Cart,Interest和order的一个field)
 */

public class ProductSelected {

    private Integer amount;
    @Transient
    private ProductEvaluate productEvaluate;
    private List<ReturnExchange> returnExchangeList;
    @Transient
    private ReturnExchange newReturnExchange;//最后一条退换货记录

    private List<HandlerInfo> handlerInfoList;
    @Transient
    private HandlerInfo newHandlerInfo;
    @Transient
    private List<String> productPropertyValueIds;
    @DBRef
    private List<ProductPropertyValue> productPropertyValueList;
    @DBRef
    private ProductSeries productSeries;
    private String receiveStatus;
    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus;
    }
    public List<ProductPropertyValue> getProductPropertyValueList() {
        return productPropertyValueList;
    }

    public void setProductPropertyValueList(List<ProductPropertyValue> productPropertyValueList) {
        this.productPropertyValueList = productPropertyValueList;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public List<String> getProductPropertyValueIds() {
        return productPropertyValueIds;
    }

    public void setProductPropertyValueIds(List<String> productPropertyValueIds) {
        this.productPropertyValueIds = productPropertyValueIds;
    }

    public ProductEvaluate getProductEvaluate() {
        return productEvaluate;
    }

    public void setProductEvaluate(ProductEvaluate productEvaluate) {
        this.productEvaluate = productEvaluate;
    }

    public ProductSeries getProductSeries() {
        return productSeries;
    }

    public void setProductSeries(ProductSeries productSeries) {
        this.productSeries = productSeries;
    }
    public List<ReturnExchange> getReturnExchangeList() {
        return returnExchangeList;
    }

    public void setReturnExchangeList(List<ReturnExchange> returnExchangeList) {
        this.returnExchangeList = returnExchangeList;
    }

    public ReturnExchange getNewReturnExchange() {
        return newReturnExchange;
    }

    public void setNewReturnExchange(ReturnExchange newReturnExchange) {
        this.newReturnExchange = newReturnExchange;
    }

    public List<HandlerInfo> getHandlerInfoList() {
        return handlerInfoList;
    }

    public void setHandlerInfoList(List<HandlerInfo> handlerInfoList) {
        this.handlerInfoList = handlerInfoList;
    }

    @Override
    public boolean equals(Object obj) {
        Assert.notNull(obj);
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ProductSelected other = (ProductSelected) obj;
        Assert.notNull(this.productSeries.getId());
        Assert.notNull(other.productSeries.getId());
        if (!this.getProductSeries().getId().equalsIgnoreCase(other.getProductSeries().getId())) return false;
        if ((other.getProductPropertyValueList() == null||other.getProductPropertyValueList().size()==0) && (this.getProductPropertyValueList() == null||this.getProductPropertyValueList().size()==0)) return true;
        if ((other.getProductPropertyValueList() == null||other.getProductPropertyValueList().size()==0) && this.getProductPropertyValueList().size()>0) return false;
        if (other.getProductPropertyValueList().size()>0 && (this.getProductPropertyValueList() == null||this.getProductPropertyValueList().size()==0)) return false;
        if (other.getProductPropertyValueList().size()!= this.getProductPropertyValueList().size()) return false;
        for (ProductPropertyValue thisProductPropertyValue : this.getProductPropertyValueList()) {
            boolean equals=false;
            for (ProductPropertyValue otherProductPropertyValue : other.getProductPropertyValueList()) {
                if (thisProductPropertyValue.getId().equalsIgnoreCase(otherProductPropertyValue.getId())) {
                    equals=true;
                }
            }
            if (!equals) return false;
        }
        return true;
    }

    public HandlerInfo getNewHandlerInfo() {
        return newHandlerInfo;
    }

    public void setNewHandlerInfo(HandlerInfo newHandlerInfo) {
        this.newHandlerInfo = newHandlerInfo;
    }
}
