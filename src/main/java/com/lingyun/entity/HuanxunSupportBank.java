package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * db.account.update({},{"$set":{"accountName":"谢宇星"}},false,true)
 * db.account.update({},{"$set":{"accountLoginName":"18974989697"}},false,true)
 */
@Document(collection = "huanxunSupportBank")
public class HuanxunSupportBank {
    @Id
    private String id;
    @Indexed
    private String bankName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
