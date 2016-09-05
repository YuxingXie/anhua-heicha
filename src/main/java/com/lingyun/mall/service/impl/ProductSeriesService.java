package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductCategory;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSubCategory;
import com.lingyun.mall.dao.ProductSeriesDao;
import com.lingyun.mall.service.IProductSeriesService;
import com.lingyun.support.vo.Sortable;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/6/11.
 */
@Service
public class ProductSeriesService extends BaseEntityManager<ProductSeries> implements IProductSeriesService {
    @Resource
    private ProductSeriesDao productSeriesDao;
    protected EntityDao<ProductSeries> getEntityDao() {
        return this.productSeriesDao;
    }

    @Override
    public List<String[]> getTop3ProductSeries() {
        return productSeriesDao.getTop3ProductSeries();
    }
    @Override
    public List<String[]> getTop3ProductSeriesDemo() {
        return productSeriesDao.getTop3ProductSeriesDemo();
    }

    @Override
    public ProductSeries findProductSeriesById(ObjectId objectId) {
        return productSeriesDao.findProductSeriesById(objectId);
    }
    @Override
    public ProductSeries findProductSeriesById(String id) {
        return productSeriesDao.findProductSeriesById(id);
    }
    @Override
    public ProductSeries findProductSeriesByIdButEvaluate(String id,boolean ignoreEvaluate) {
        return productSeriesDao.findProductSeriesByIdButEvaluate(new ObjectId(id), ignoreEvaluate);
    }
    @Override
    public Page<ProductSeries> findProductSeriesesByKeyWord(String keyWord, int currentPage,int pageSize) {
        return productSeriesDao.findProductSeriesPageByKeyWord(keyWord, currentPage, pageSize);
    }

    @Override
    public Page<ProductSeries> findProductSeriesPageByProductSubCategory(ProductSubCategory productSubCategory, Integer page, int pageSize) {
        return productSeriesDao.findProductSeriesPageByProductSubCategory(productSubCategory,page,pageSize);
    }

    @Override
    public Page<ProductSeries> findProductSeriesPageByProductSubCategorySortByPrice(ProductSubCategory productSubCategory, int page, int size, Sortable sort) {
        return productSeriesDao.findProductSeriesPageByProductSubCategorySortByPrice(productSubCategory, page, size, sort);
    }

    @Override
    public Page<ProductSeries> findProductSeriesPageByProductSubCategoryWithSort(ProductSubCategory productSubCategory, int page, int size, Sortable sort) {
        return productSeriesDao.findProductSeriesPageByProductSubCategoryWithSort(productSubCategory, page, size, sort);
    }
    @Override
    public List<ProductSeries> getNewProducts(int count) {
        return productSeriesDao.getNewProducts(count);
    }

    @Override
    public List<ProductSeries> getLowPrices(int count) {
        return productSeriesDao.getLowPrices(count);
    }
    @Override
    public List<ProductSeries> getHotSell(int count) {
        return productSeriesDao.getHotSell(count);
    }

    @Override
    public List<ProductSeries> findProductSeriesAllRef(DBObject dbObject) {
        return productSeriesDao.findProductSeriesAllRef(dbObject);
    }

    @Override
    public List<ProductSeries> findProductSeriesByProductCategory(ProductCategory productCategory) {
        return productSeriesDao.findProductSeriesByProductCategory(productCategory) ;
    }

    @Override
    public List<ProductSeries> findProductSeriesByProductSubCategory(ProductSubCategory productSubCategory) {
        return productSeriesDao.findProductSeriesByProductSubCategory(productSubCategory) ;
    }

    @Override
    public List<ProductSeries> findProductSeriesByName(String name) {
        return productSeriesDao.findProductSeriesByName(name);
    }

    @Override
    public void removeProductSeriesAndPictures(ProductSeries productSeries) {
        productSeriesDao.removeProductSeriesAndPictures(productSeries);
    }
}
