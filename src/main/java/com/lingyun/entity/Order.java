package com.lingyun.entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
@Document(collection = "order")
public class Order {
    @Id
    private String id;
    @Field
    private List<ProductSelected> productSelectedList;
    @Field(value = "payStatus")
    /**
     * 支付成功:y，支付失败n，为空表示未进行支付
     */
    private String payStatus;
    @Field(value = "payWay")
    @NotEmpty
    /**
     * 支付方式，货到付款1，在线支付2，公司转账3，邮局汇款4
     */
    private String payWay;
    /**
     * 收件人是否本人
     */
    @Field
    private boolean self;
    /**
     * 订单是否开始处理
     */
    private Boolean handler;
    private Date handlerDate;
    @DBRef
    private Administrator handlerAdmin;
    @Transient
    private String payWayString;
    @Field(value = "orderDate")
    private Date orderDate;
    @Field
    private Date payDate;
    @Field(value = "acceptAddress")
    @NotEmpty
    private String acceptAddress;

    @Field(value = "submitStatus")
    private String submitStatus;
    @NotEmpty
    @Field(value = "acceptPersonName")
    private String acceptPersonName;
    @Field(value = "contactPhone")
    private String contactPhone;
//    @Field(value = "payAccountId")
//    private String payAccountId;
    @Transient
    private Double totalPrice;
    @Transient
    private Integer totalAmount;
    @DBRef
    private User user;
    @DBRef(db = "account")
    private Account payAccount;
    @Transient
    private String receiveStatus;//全部未确认收货none,部分确认:part,全部确认:all
    @Transient
    private String evaluateStatus;//全部未评价none,部分:part,全部:all

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public String getReceiveStatus() {
        Assert.notNull(this.productSelectedList);
        boolean all=true,none=true;
        for(ProductSelected productSelected:this.productSelectedList){
            if (productSelected.getReceiveStatus()==null||productSelected.getReceiveStatus().equalsIgnoreCase("n")) all=false;
            else none= false;
        }
        if (all) return "all";
        if (none) return "none";
        return "part";
    }

    public String getEvaluateStatus() {
        Assert.notNull(this.getProductSelectedList());
        boolean all=true,none=true;
        for(ProductSelected productSelected:this.getProductSelectedList()){
            if (productSelected.getProductEvaluate()==null) all=false;
            else none= false;
        }
        if (all) return "all";
        if (none) return "none";
        return "part";
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Double getTotalPrice() {
        Double price=0d;
        if (productSelectedList==null) return price;
        for (ProductSelected productSelected:productSelectedList){
            Double thePrice=productSelected.getProductSeries()==null?0:(productSelected.getProductSeries().getCommonPrice()==null?0:productSelected.getProductSeries().getCommonPrice());
            price+=thePrice*productSelected.getAmount();
        }
        return price;
    }

    public Integer getTotalAmount() {
        Integer totalAmount=0;
        if (productSelectedList==null) return totalAmount;
        for (ProductSelected productSelected:productSelectedList){
            totalAmount+=productSelected.getAmount();
        }
        return totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public List<ProductSelected> getProductSelectedList() {
        if (productSelectedList!=null &&productSelectedList.size()>0){
            boolean hasEvaluated=false;
            ProductSeries evaluatedProductSeries=null;
            ProductEvaluate evaluate=null;
            for (ProductSelected productSelected:productSelectedList){
                if (productSelected.getProductEvaluate()!=null){
                    hasEvaluated=true;
                    evaluatedProductSeries=productSelected.getProductSeries();
                    evaluate=productSelected.getProductEvaluate();
                    break;
                }
            }
            if (hasEvaluated){
                for (ProductSelected productSelected:productSelectedList){
                    if (evaluatedProductSeries.getId().equalsIgnoreCase(productSelected.getProductSeries().getId())){
                        if (productSelected.getProductEvaluate()==null){
                            productSelected.setProductEvaluate(evaluate);
                        }
                    }
                }
            }
        }
        return productSelectedList;
    }

    public String getAcceptPersonName() {
        return acceptPersonName;
    }

    public void setAcceptPersonName(String acceptPersonName) {
        this.acceptPersonName = acceptPersonName;
    }

    public void setProductSelectedList(List<ProductSelected> productSelectedList) {
        this.productSelectedList = productSelectedList;
    }


    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public String getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(String submitStatus) {
        this.submitStatus = submitStatus;
    }


//    public String getPayAccountId() {
//        return payAccountId;
//    }
//
//    public void setPayAccountId(String payAccountId) {
//        this.payAccountId = payAccountId;
//    }

    public Account getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(Account payAccount) {
        this.payAccount = payAccount;
    }

    public Boolean getHandler() {
        return handler;
    }

    public void setHandler(Boolean handler) {
        this.handler = handler;
    }

    public Date getHandlerDate() {
        return handlerDate;
    }

    public void setHandlerDate(Date handlerDate) {
        this.handlerDate = handlerDate;
    }

    public Administrator getHandlerAdmin() {
        return handlerAdmin;
    }

    public void setHandlerAdmin(Administrator handlerAdmin) {
        this.handlerAdmin = handlerAdmin;
    }

    //货到付款1，在线支付2，公司转账3，邮局汇款4
    public String getPayWayString() {
        return payWayString==null?null:(payWayString.equals("1")?"货到付款":(payWayString.equals("2")?"在线支付":(payWayString.equals("3")?"公司转账":(payWayString.equals("4")?"邮局汇款":null))));
    }

}
