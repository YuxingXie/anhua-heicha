package com.lingyun.entity;



/**
 * 订单提交信息，包括联系人，联系方式，联系电话，支付方式，配送地址等
 */
public class OrderSubmitInfo {


    /**
     * 支付方式，货到付款1，在线支付2，公司转账3，邮局汇款4
     */
    private String payWay;
    /**
     * 收件人是否本人
     */
    private boolean self;



    private String payWayString;



    private String acceptAddress;


    private String submitStatus;

    private String acceptPersonName;

    private String contactPhone;





    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }


    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }


    public String getAcceptPersonName() {
        return acceptPersonName;
    }

    public void setAcceptPersonName(String acceptPersonName) {
        this.acceptPersonName = acceptPersonName;
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


    //货到付款1，在线支付2，公司转账3，邮局汇款4
    public String getPayWayString() {
        return payWayString==null?null:(payWayString.equals("1")?"货到付款":(payWayString.equals("2")?"在线支付":(payWayString.equals("3")?"公司转账":(payWayString.equals("4")?"邮局汇款":null))));
    }

}
