package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/14.
 */
@Document(collection = "salesCampaign")
public class SalesCampaign {
    @Id
    private String id;
    @Field
    private String name;
    @Field
    private List<String> participants;//对应用户类型
    @Field
    private Date beginDate;
    @Field
    private Date endDate;
    @Field
    private Double commonRate;//通用折扣
    @Field
    private Boolean allProductParticipateIn;//是否所有商品券参与

    private List<ProductSeriesRate> productSeriesRates;//每个产品的折扣，优先于通用折扣
    @Field
    private String comment;
    @Field
    private String url;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
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

    public Double getCommonRate() {
        return commonRate;
    }

    public void setCommonRate(Double commonRate) {
        this.commonRate = commonRate;
    }

    public Boolean getAllProductParticipateIn() {
        return allProductParticipateIn;
    }

    public void setAllProductParticipateIn(Boolean allProductParticipateIn) {
        this.allProductParticipateIn = allProductParticipateIn;
    }

    public List<ProductSeriesRate> getProductSeriesRates() {
        return productSeriesRates;
    }

    public void setProductSeriesRates(List<ProductSeriesRate> productSeriesRates) {
        this.productSeriesRates = productSeriesRates;
    }

    public boolean isNow() {
        Date now=new Date();
        Assert.notNull(beginDate);
        Assert.notNull(endDate);
        return now.after(beginDate)&&now.before(endDate);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
