package com.lingyun.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * 参与活动的某个产品的折扣
 */
public class ProductSeriesRate {

    private SalesCampaign salesCampaign;
    @DBRef
    private ProductSeries productSeries;
    private Double rate;

    public SalesCampaign getSalesCampaign() {
        return salesCampaign;
    }

    public void setSalesCampaign(SalesCampaign salesCampaign) {
        this.salesCampaign = salesCampaign;
    }

    public ProductSeries getProductSeries() {
        return productSeries;
    }

    public void setProductSeries(ProductSeries productSeries) {
        this.productSeries = productSeries;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
