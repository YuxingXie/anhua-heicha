package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.ProductCategory;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSubCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
@Repository
public class ProductCategoryDao extends BaseMongoDao<ProductCategory> {
    private static Logger logger = LogManager.getLogger();
    public String getProductCategoryIdByProductSeriesId(String productSeriesId) {
        if (productSeriesId==null) return null;
        ProductSeries productSeries= ServiceManager.productSeriesService.findById(productSeriesId);
        if (productSeries==null) return null;
        ProductSubCategory productSubCategory=ServiceManager.productSubCategoryService.findById(productSeries.getProductSubCategory().getId());
        return productSubCategory==null?null:productSubCategory.getId();
    }

    public List<ProductCategory> findAllCategories() {
        List<ProductCategory> productCategories=findAll();
        for (ProductCategory productCategory:productCategories){
//            System.out.println("大类名称："+productCategory.getCategoryName());
            List<ProductSubCategory> productSubCategories=ServiceManager.productSubCategoryService.getProductSubCategoriesByCategoryId(productCategory.getId());
            for (ProductSubCategory productSubCategory:productSubCategories){
//                System.out.println("小类名称："+productSubCategory.getSubCategoryName());
            }
            productCategory.setProductSubCategories(productSubCategories);
        }
        return productCategories;
    }
@Deprecated
    public void removeByProductSeries(ProductSeries productSeries) {

    }
}
