package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2015/6/4.
 */
@Document(collection = "productProperty")
public class ProductProperty {
    @Id
    private String id;
    private String propertyName;
    @DBRef
    @NotNull
    private ProductSeries productSeries;
    @Transient
    private List<ProductPropertyValue> propertyValues;
    public List<ProductPropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<ProductPropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public ProductSeries getProductSeries() {
        return productSeries;
    }

    public void setProductSeries(ProductSeries productSeries) {
        this.productSeries = productSeries;
    }
}
