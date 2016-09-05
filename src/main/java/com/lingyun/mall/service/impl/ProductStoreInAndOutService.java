package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductStoreInAndOut;
import com.lingyun.mall.dao.ProductStoreInAndOutDao;
import com.lingyun.mall.service.IProductStoreInAndOutService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductStoreInAndOutService extends BaseEntityManager<ProductStoreInAndOut> implements IProductStoreInAndOutService {
    @Resource
    private ProductStoreInAndOutDao productStoreInAndOutDao;
    @Override
    protected EntityDao<ProductStoreInAndOut> getEntityDao() {
        return this.productStoreInAndOutDao;
    }

    @Override
    public List<ProductStoreInAndOut> findByProductSeries(ProductSeries productSeries) {
        return productStoreInAndOutDao.findByProductSeries(productSeries);
    }
    @Override
    public void clearNullUserInAndOut(){
        productStoreInAndOutDao.clearNullUserInAndOut();
    }
}
