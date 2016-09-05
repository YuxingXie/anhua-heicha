package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/20.
 */
@Document(collection = "notify")
public class Notify {
    @Id
    private String id;
    @DBRef
    private User toUser;
    @DBRef
    private Administrator toAdministrator;
    @DBRef
    private Administrator fromAdministrator;
    @DBRef
    private User fromUser;
    private String content;
    private Date date;
    private Boolean read;
    private String title;
    private Map<String,Object> importantStuffs;
    private List<String> pictures;
    private String notifyType;//administrator notify,
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
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

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getImportantStuffs() {
        return importantStuffs;
    }

    public void setImportantStuffs(Map<String, Object> importantStuffs) {
        this.importantStuffs = importantStuffs;
    }

    public Administrator getToAdministrator() {
        return toAdministrator;
    }

    public void setToAdministrator(Administrator toAdministrator) {
        this.toAdministrator = toAdministrator;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public Administrator getFromAdministrator() {
        return fromAdministrator;
    }

    public void setFromAdministrator(Administrator fromAdministrator) {
        this.fromAdministrator = fromAdministrator;
    }
}
