package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 首页区域，代表需要在首页显示的一系列商品及分类，比如热销产品，低价促销等
 */
@Document(collection = "homePageBlock")
public class HomePageBlock {
    @Id
    private String id;
    private String title;
    private String description;
    private String descriptionWhenEmpty;
    @DBRef
    private Administrator administrator;
    private Date date;
    @DBRef
    private List<ProductSeries> productSeriesList;
    private Boolean show;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionWhenEmpty() {
        return descriptionWhenEmpty;
    }

    public void setDescriptionWhenEmpty(String descriptionWhenEmpty) {
        this.descriptionWhenEmpty = descriptionWhenEmpty;
    }

    public List<ProductSeries> getProductSeriesList() {
        return productSeriesList;
    }

    public void setProductSeriesList(List<ProductSeries> productSeriesList) {
        this.productSeriesList = productSeriesList;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
