package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductProperty;
import com.lingyun.entity.ProductSeries;

import java.util.List;

/**
 * Created by Administrator on 2015/7/3.
 */
public interface IProductPropertyService  extends IBaseEntityManager<ProductProperty> {
    List<ProductProperty> getProductPropertiesByProductSeriesId(String productSeriesId);

    void removeByProductSeries(ProductSeries productSeries);
}
