package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class InterestDao  extends BaseMongoDao<Interest> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    public List<Interest> findInterestsOfUser(User user) {
        if (user==null) return null;
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",user);
        List<Interest> interests=findAll(dbObject);
        if (interests!=null){
            for (Interest interest:interests){
                ProductSeries productSeries=interest.getProductSeries();
                if (productSeries!=null){
//                    List<ProductSeriesPrice> prices= ServiceManager.productSeriesPriceService.findByProductSeriesId(productSeries.getId());
//                    productSeries.setProductSeriesPrices(prices);
//                    List<ProductStoreInAndOut> inAndOuts=ServiceManager.productStoreInAndOutService.findByProductSeries(productSeries);
//                    if(inAndOuts!=null){
//                        productSeries.getProductStore().setInAndOutList(inAndOuts);
//                    }
                    List<ProductProperty> productProperties=ServiceManager.productPropertyService.getProductPropertiesByProductSeriesId(productSeries.getId());
                    if (productProperties!=null){
                        for (ProductProperty productProperty:productProperties){
                            List<ProductPropertyValue> values=ServiceManager.productPropertyValueService.findAll(new BasicDBObject("productProperty",productProperty));
                            productProperty.setPropertyValues(values);
                        }
                    }
                    productSeries.setProductProperties(productProperties);
                }
            }
        }
        return interests;
    }

    public boolean alreadyInterested(User user,ProductSeries productSeries) {
        if (user==null) return false;
        List<Interest> interests = findByUserAndProductSeries(user, productSeries);
        if (interests==null) return false;
        if (interests.size()==0) return false;
        return true;
    }

    public List<Interest> findByUserAndProductSeries(User user, ProductSeries productSeries) {
        if (user==null) return null;
        if (productSeries==null) return null;
        DBObject dbObject=new BasicDBObject();
        dbObject.put("productSeries",productSeries);
        dbObject.put("user",user);
        return findAll(dbObject);
    }

    public List<Interest> findByProductSeries(ProductSeries productSeries) {
        if (productSeries==null) return null;
        DBObject dbObject=new BasicDBObject();
        dbObject.put("productSeries",productSeries);
        return findAll(dbObject);
    }
}
