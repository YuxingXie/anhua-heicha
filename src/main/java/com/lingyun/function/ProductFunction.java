package com.lingyun.function;

import com.lingyun.common.helper.service.ProjectContext;
import com.lingyun.entity.ProductCategory;
import com.lingyun.entity.ProductProperty;
import com.lingyun.entity.ProductSubCategory;
import com.lingyun.mall.service.IProductCategoryService;
import com.lingyun.mall.service.IProductSubCategoryService;
import com.lingyun.mall.service.impl.ProductPropertyService;

import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
public class ProductFunction {
    public static List<ProductProperty> getProductPropertiesByProductSeriesId(String productSeriesId){
        if (productSeriesId==null) return null;
        ProductPropertyService productPropertyService=ProjectContext.getBean("productPropertyService");
        return productPropertyService.getProductPropertiesByProductSeriesId(productSeriesId);
    }
    public static List<ProductCategory> getProductCategories(){
        IProductCategoryService productCategoryService=ProjectContext.getBean("productCategoryService");
        return productCategoryService.findAll();
    }
    public static String getProductCategoryIdByProductSeriesId(String productSeriesId){
        IProductCategoryService productCategoryService=ProjectContext.getBean("productCategoryService");
        return productCategoryService.getProductCategoryIdByProductSeriesId(productSeriesId);
    }

    public static List<ProductSubCategory> getProductSubCategoriesByCategoryId(String categoryId){
        if (categoryId==null) return null;
        IProductSubCategoryService productSubCategoryService=ProjectContext.getBean("productSubCategoryService");
        List<ProductSubCategory> list= productSubCategoryService.getProductSubCategoriesByCategoryId(categoryId);
        return list;
    }
}
