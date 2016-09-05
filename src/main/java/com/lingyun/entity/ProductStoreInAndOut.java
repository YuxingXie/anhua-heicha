package com.lingyun.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

//@Document(collection = "productStoreInAndOut")
public class ProductStoreInAndOut {

    @Field
    private String type;//in,out
    @Field
    private Integer amount;
    @Field
    private Date date;

    @DBRef
    private ProductPropertyValue productPropertyValue;
    @DBRef
    private Administrator operator;

    public ProductPropertyValue getProductPropertyValue() {
        return productPropertyValue;
    }

    public void setProductPropertyValue(ProductPropertyValue productPropertyValue) {
        this.productPropertyValue = productPropertyValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Administrator getOperator() {
        return operator;
    }

    public void setOperator(Administrator operator) {
        this.operator = operator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
