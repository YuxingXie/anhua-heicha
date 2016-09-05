package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.SalesCampaign;
import com.lingyun.mall.dao.SalesCampaignDao;
import com.lingyun.mall.service.ISalesCampaignService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2016/1/7.
 */
@Service
public class SalesCampaignService extends BaseEntityManager<SalesCampaign> implements ISalesCampaignService {
    @Resource
    private SalesCampaignDao salesCampaignDao;
    protected EntityDao<SalesCampaign> getEntityDao() {
        return this.salesCampaignDao;
    }

    @Override
    public void removeProductSeries(ProductSeries productSeries) {
        salesCampaignDao.removeProductSeries(productSeries);
    }
}
