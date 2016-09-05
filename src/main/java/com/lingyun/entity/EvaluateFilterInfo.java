package com.lingyun.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/26.
 */
public class EvaluateFilterInfo {
    private Boolean forbid;
    private String messageWhenForbid;
    @DBRef
    private Administrator administrator;
    private String reason;
    private Date date;

    public Boolean getForbid() {
        return forbid;
    }

    public void setForbid(Boolean forbid) {
        this.forbid = forbid;
    }

    public String getMessageWhenForbid() {
        return messageWhenForbid;
    }

    public void setMessageWhenForbid(String messageWhenForbid) {
        this.messageWhenForbid = messageWhenForbid;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
