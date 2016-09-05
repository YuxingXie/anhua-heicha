package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.SalesCampaign;

/**
 * Created by Administrator on 2016/1/7.
 */
public interface ISalesCampaignService extends IBaseEntityManager<SalesCampaign> {
    void removeProductSeries(ProductSeries productSeries);
}
