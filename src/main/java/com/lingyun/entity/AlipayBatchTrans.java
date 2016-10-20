package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */
@Document(collection = "alipayBatchTrans")
public class AlipayBatchTrans {
    @Id
    private String id;
    private String payDate;//payDate+batchNoSn=batch_no
    private Date date;//payDate作为支付宝支付网关参数，而此date作为系统记录的插入时间
    private Date finishDate;//系统记录的支付宝回调时间
    private int batchNoSn;
    private double batchFee;
    private int batchNum;
    private String detailData;
    @Transient
    private List<AlipayTrans> alipayTransList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public int getBatchNoSn() {
        return batchNoSn;
    }

    public void setBatchNoSn(int batchNoSn) {
        this.batchNoSn = batchNoSn;
    }

    public double getBatchFee() {
        return batchFee;
    }

    public void setBatchFee(double batchFee) {
        this.batchFee = batchFee;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    public String getDetailData() {
        return detailData;
    }

    public void setDetailData(String detailData) {
        this.detailData = detailData;
    }

    public List<AlipayTrans> getAlipayTransList() {
        return alipayTransList;
    }

    public void setAlipayTransList(List<AlipayTrans> alipayTransList) {
        this.alipayTransList = alipayTransList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
