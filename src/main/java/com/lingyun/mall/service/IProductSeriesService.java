package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductCategory;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSubCategory;
import com.lingyun.support.vo.Sortable;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2015/6/11.
 */
public interface IProductSeriesService  extends IBaseEntityManager<ProductSeries> {
    List<String[]> getTop3ProductSeries();
    List<String[]> getTop3ProductSeriesDemo();

    List<ProductSeries> getHotSell(int count);

    ProductSeries findProductSeriesById(ObjectId objectId);
    ProductSeries findProductSeriesById(String id);
    ProductSeries findProductSeriesByIdButEvaluate(String id,boolean ignoreEvaluate);
    Page<ProductSeries> findProductSeriesesByKeyWord(String keyWord, int currentPage,int pageSize);
    Page<ProductSeries> findProductSeriesPageByProductSubCategory(ProductSubCategory productSubCategory, Integer page, int pageSize);
    Page<ProductSeries> findProductSeriesPageByProductSubCategorySortByPrice(ProductSubCategory productSubCategory, int page, int size, Sortable sort);
    Page<ProductSeries> findProductSeriesPageByProductSubCategoryWithSort(ProductSubCategory productSubCategory, int page, int size, Sortable sort);

    List<ProductSeries> getNewProducts(int count);

    List<ProductSeries> getLowPrices(int count);
    List<ProductSeries> findProductSeriesAllRef(DBObject dbObject);


    List<ProductSeries> findProductSeriesByProductCategory(ProductCategory productCategory);

    List<ProductSeries> findProductSeriesByProductSubCategory(ProductSubCategory productSubCategory);

    List<ProductSeries> findProductSeriesByName(String name);

    void removeProductSeriesAndPictures(ProductSeries productSeries);
}
