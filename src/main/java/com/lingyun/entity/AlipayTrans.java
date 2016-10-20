package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "alipayTrans")
public class AlipayTrans {
    @Id
    private String id;


    private Date date;

    //支付状态，见AlipayTransStatusEnum
    private int alipayTransStatus;

    private String note;

    //提款金额
    private double fee;

    @DBRef
    private Account account;
    @DBRef
    private AlipayBatchTrans alipayBatchTrans;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public AlipayBatchTrans getAlipayBatchTrans() {
        return alipayBatchTrans;
    }

    public void setAlipayBatchTrans(AlipayBatchTrans alipayBatchTrans) {
        this.alipayBatchTrans = alipayBatchTrans;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAlipayTransStatus() {
        return alipayTransStatus;
    }

    public void setAlipayTransStatus(int alipayTransStatus) {
        this.alipayTransStatus = alipayTransStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
