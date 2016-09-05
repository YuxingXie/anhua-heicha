package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.HomePageBlock;
import com.lingyun.entity.ProductSeries;
import com.lingyun.mall.dao.HomePageBlockDao;
import com.lingyun.mall.service.IHomePageBlockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/30.
 */
@Service
public class HomePageBlockService extends BaseEntityManager<HomePageBlock> implements IHomePageBlockService {
    @Resource
    private HomePageBlockDao homePageBlockDao;
    protected EntityDao<HomePageBlock> getEntityDao() {
        return this.homePageBlockDao;
    }

    @Override
    public void removeProductSeries(ProductSeries productSeries) {
        homePageBlockDao.removeProductSeries(productSeries);
    }
}
