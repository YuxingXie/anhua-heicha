package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductCategory;
import com.lingyun.entity.ProductSeries;

import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
public interface IProductCategoryService extends IBaseEntityManager<ProductCategory> {
    String getProductCategoryIdByProductSeriesId(String productSeriesId);

    List<ProductCategory> findAllCategories();
@Deprecated
    void removeByProductSeries(ProductSeries productSeries);
}
