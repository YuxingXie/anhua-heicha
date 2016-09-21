package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by Administrator on 2015/12/15.
 * 合作商城信息
 */
@Document(collection = "friendshipMall")
//db.friendshipMall.insert({"name":"栀子花商城","ico":"statics/img/malls/33110.gif","url":"http://cn.bing.com","valid":true})
//db.friendshipMall.update({"name":"栀子花商城"},{"$set":{"exchangeUrl":"http://csayin.com:8084/xyAppServer/otherCompanyUserImport"}},false,true)
//db.friendshipMall.update({"name":"栀子花商城"},{"$set":{"url":"http://csayin.com:8084/xyAppServer"}},false,true)
//db.friendshipMall.update({"name":"栀子花商城"},{"$set":{"name":"想呗商城","ico":"statics/img/malls/xiangbeiAPP.png","url":"http://csayin.com:8084/xyAppServer"}},false,true)
//db.friendshipMall.insert({"name":"京东商城","ico":"statics/img/malls/65d7511fbde32715f147024ae9d4dbea_121_121.jpg","url":"http://www.jd.com","valid":true})
public class FriendshipMall {
    @Id
    private String id;

    @Field
    private String name;
    @Field
    private String ico;
    @Field
    private String url;
    @Field
    private String exchangeUrl;
    @Field
    private boolean valid;
    @Field
    private String virtualMoneyName;
    public String getVirtualMoneyName() {
        return virtualMoneyName;
    }

    public void setVirtualMoneyName(String virtualMoneyName) {
        this.virtualMoneyName = virtualMoneyName;
    }
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

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getExchangeUrl() {
        return exchangeUrl;
    }

    public void setExchangeUrl(String exchangeUrl) {
        this.exchangeUrl = exchangeUrl;
    }
}
