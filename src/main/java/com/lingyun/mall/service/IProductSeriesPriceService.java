package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSeriesPrice;
import com.lingyun.entity.ProductSubCategory;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2015/11/16.
 */
public interface IProductSeriesPriceService extends IBaseEntityManager<ProductSeriesPrice> {
    List<ProductSeriesPrice> findByProductSeriesId(String id);
    ProductSeriesPrice findCurrentPriceByProductSeriesId(String productSeriesId);
    Page<ProductSeries> getProductSeriesOrderByPriceInProductSubCategory(ProductSubCategory productSubCategory,Integer currentPage,Integer pageSize,boolean asc);
}
