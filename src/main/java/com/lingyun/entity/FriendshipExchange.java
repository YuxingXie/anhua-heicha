package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 * 合作商城购物兑换红包信息
 */
@Document(collection = "friendshipExchange")

public class FriendshipExchange {
    @Id
    private String id;
    @Field
    private int pointCount;
   @DBRef
    private FriendshipMall mall;
    @DBRef
    private User user;
     @Field
    private Date date;
    @Field
    private Map<String,Object> returnValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public FriendshipMall getMall() {
        return mall;
    }

    public void setMall(FriendshipMall mall) {
        this.mall = mall;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, Object> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Map<String, Object> returnValue) {
        this.returnValue = returnValue;
    }

}
