package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * Created by Administrator on 2015/6/4.
 */
@Document(collection = "productPropertyValue")
public class ProductPropertyValue {
    @Id
    private String id;
    @Field
    private String value;
//    @Transient
//    private ProductSeries productSeries;
    @DBRef
    private ProductProperty productProperty;




//    public ProductSeries getProductSeries() {
//        return productSeries;
//    }
//
//    public void setProductSeries(ProductSeries productSeries) {
//        this.productSeries = productSeries;
//    }

    public ProductProperty getProductProperty() {
        return productProperty;
    }

    public void setProductProperty(ProductProperty productProperty) {
        this.productProperty = productProperty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
