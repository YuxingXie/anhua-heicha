package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
@Document(collection = "productEvaluate")
public class ProductEvaluate {
    @Id
    private String id;
    private String content;
    private List<String> pictures;
    private Integer grade;
    private Date date;
    private String type;//praise赞,evaluate评论,reply评论的回复
    private Boolean anonymous;
    @DBRef
    private ProductEvaluate parent;
    @Transient
    private List<ProductEvaluate> replies;
    @Transient
    private List<ProductEvaluate> praises;
    @DBRef
    private ProductSeries productSeries;
    @DBRef
    private Order order;
    @Transient
    private String orderId;
    @Transient
    private String productSeriesId;
    @Transient
    private User user;
    @DBRef
    private User replyUser;

    private EvaluateFilterInfo evaluateFilterInfo;

    public User getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(User replyUser) {
        this.replyUser = replyUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductSeriesId() {
        return productSeriesId;
    }

    public void setProductSeriesId(String productSeriesId) {
        this.productSeriesId = productSeriesId;
    }

    public ProductSeries getProductSeries() {
        return productSeries;
    }

    public void setProductSeries(ProductSeries productSeries) {
        this.productSeries = productSeries;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public ProductEvaluate getParent() {
        return parent;
    }

    public void setParent(ProductEvaluate parent) {
        this.parent = parent;
    }

    public List<ProductEvaluate> getReplies() {
        return replies;
    }

    public void setReplies(List<ProductEvaluate> replies) {
        this.replies = replies;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ProductEvaluate> getPraises() {
        return praises;
    }

    public void setPraises(List<ProductEvaluate> praises) {
        this.praises = praises;
    }

    public EvaluateFilterInfo getEvaluateFilterInfo() {
        return evaluateFilterInfo;
    }

    public void setEvaluateFilterInfo(EvaluateFilterInfo evaluateFilterInfo) {
        this.evaluateFilterInfo = evaluateFilterInfo;
    }
}
