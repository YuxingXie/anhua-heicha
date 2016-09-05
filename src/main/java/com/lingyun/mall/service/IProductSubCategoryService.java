package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSubCategory;

import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
public interface IProductSubCategoryService extends IBaseEntityManager<ProductSubCategory> {
    List<ProductSubCategory> getProductSubCategoriesByCategoryId(String categoryId);

    ProductSubCategory getProductSubCategoriesByProductSeries(ProductSeries productSeries);

    ProductSubCategory findProductSubCategoryById(String id);

    ProductSubCategory findProductSubCategoryByIdWithoutProductSeries(String id);

@Deprecated
    void removeByProductSeries(ProductSeries productSeries);
}
