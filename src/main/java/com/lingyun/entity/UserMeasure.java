package com.lingyun.entity;

import com.lingyun.support.yexin.PairTouchModeMemberRank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
/*
db.userMeasure.insert({"count":10,"date":new Date(),"type":1,"sort":1,"user":new DBRef("mallUser", ObjectId("591e656a5d94c42611e949c3"))})
db.userMeasure.update({"_id":ObjectId("590a1398a7e9bc0a6c184082")},{"$set":{"count":1000}},false,true)

 */
@Document(collection = "userMeasure")
public class UserMeasure {
    @Id
    private String id;
    @Field
    private double count;
    @Field
    private Date date;
    @Field
    private int type;//1:收入，-1:支出
    @Field
    private int sort;//见com.lingyun.common.code.UserMeasureSortEnum
    @Field
    private String note;
    @Field
    private boolean isMaterial ;//是否实物，为false表示现金
    @Field
    private String materialNote;//实物说明，比如：华为手机一部
    @Field
    private PairTouchModeMemberRank rank;
    @DBRef
    private User user;
    @DBRef
    private User fromUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaterialNote() {
        return materialNote;
    }

    public void setMaterialNote(String materialNote) {
        this.materialNote = materialNote;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMaterial() {
        return isMaterial;
    }

    public void setMaterial(boolean isMaterial) {
        this.isMaterial = isMaterial;
    }

    public PairTouchModeMemberRank getRank() {
        return rank;
    }

    public void setRank(PairTouchModeMemberRank rank) {
        this.rank = rank;
    }
}