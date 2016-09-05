package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductCategory;
import com.lingyun.entity.ProductSeries;
import com.lingyun.mall.dao.ProductCategoryDao;
import com.lingyun.mall.service.IProductCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
@Service
public class ProductCategoryService extends BaseEntityManager<ProductCategory> implements IProductCategoryService {
    @Resource
    private ProductCategoryDao productCategoryDao;
    protected EntityDao<ProductCategory> getEntityDao() {
        return this.productCategoryDao;
    }

    @Override
    public String getProductCategoryIdByProductSeriesId(String productSeriesId) {
        return productCategoryDao.getProductCategoryIdByProductSeriesId(productSeriesId);
    }

    @Override
    public List<ProductCategory> findAllCategories() {
        return productCategoryDao.findAllCategories();
    }

    @Override
    public void removeByProductSeries(ProductSeries productSeries) {
        productCategoryDao.removeByProductSeries(productSeries);
    }
}
