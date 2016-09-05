package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
@Document(collection = "productSubCategory")
public class ProductSubCategory {
    @Id
    private String id;
    @DBRef(db = "productCategory")
    private ProductCategory productCategory;
    @Field(value = "subCategoryName")
    private String subCategoryName;
    @Transient
    private List<ProductSeries> productSeriesList;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public List<ProductSeries> getProductSeriesList() {
        return productSeriesList;
    }

    public void setProductSeriesList(List<ProductSeries> productSeriesList) {
        this.productSeriesList = productSeriesList;
    }
}
