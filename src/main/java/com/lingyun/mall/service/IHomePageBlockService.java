package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.HomePageBlock;
import com.lingyun.entity.ProductSeries;

/**
 * Created by Administrator on 2015/12/30.
 */
public interface IHomePageBlockService  extends IBaseEntityManager<HomePageBlock> {
    void removeProductSeries(ProductSeries productSeries);
}
