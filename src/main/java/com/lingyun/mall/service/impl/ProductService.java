package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.mall.dao.ProductSeriesDao;
import com.lingyun.mall.service.IProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/7/1.
 */
@Service
public class ProductService extends BaseEntityManager<ProductSeries> implements IProductService {
    @Resource
    private ProductSeriesDao productSeriesDao;
    protected EntityDao<ProductSeries> getEntityDao() {
        return this.productSeriesDao;
    }
}
