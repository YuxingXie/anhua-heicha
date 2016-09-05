package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Order;
import com.lingyun.entity.ProductEvaluate;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.User;
import com.lingyun.mall.dao.ProductEvaluateDao;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductEvaluateService extends BaseEntityManager<ProductEvaluate> implements IProductEvaluateService {
    @Resource
    private ProductEvaluateDao productEvaluateDao;
    protected EntityDao<ProductEvaluate> getEntityDao() {
        return this.productEvaluateDao;
    }

    @Override
    public ProductEvaluate findByOrderAndProductSeries(Order order, ProductSeries productSeries) {
        return productEvaluateDao.findByOrderAndProductSeries(order, productSeries);
    }

    @Override
    public Page<ProductEvaluate> findProductEvaluatesPageByProductSeries(ProductSeries productSeries, Integer page, int pageSize) {
        return productEvaluateDao.findProductEvaluatesPageByProductSeries(productSeries, page, pageSize) ;
    }

    @Override
    public Page<ProductEvaluate> findProductEvaluatesPageWithoutParentEvaluateByProductSeries(ProductSeries productSeries, Integer page, int pageSize) {
        return productEvaluateDao.findProductEvaluatesPageWithoutParentEvaluateByProductSeries(productSeries, page, pageSize);
    }

    @Override
    public List<ProductEvaluate> findEvaluatesWithParentId(String evaluateId) {
        return productEvaluateDao.findEvaluatesWithParentId(evaluateId);
    }

    @Override
    public boolean praised(ProductEvaluate parent, User praiseUser) {
        return productEvaluateDao.praised(parent, praiseUser);
    }

    @Override
    public ProductEvaluate praisedEvaluate(ProductEvaluate parent, User praiseUser) {
        return productEvaluateDao.praisedEvaluate(parent,praiseUser);
    }

    @Override
    public void removeByProductSeries(ProductSeries productSeries) {
        productEvaluateDao.removeByProductSeries(productSeries);
    }
}
