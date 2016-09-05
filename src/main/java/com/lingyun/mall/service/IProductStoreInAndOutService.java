package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductStoreInAndOut;

import java.util.List;

/**
 * Created by Administrator on 2015/11/23.
 */
public interface IProductStoreInAndOutService extends IBaseEntityManager<ProductStoreInAndOut> {
    List<ProductStoreInAndOut> findByProductSeries(ProductSeries productSeries);
    void clearNullUserInAndOut();
}
