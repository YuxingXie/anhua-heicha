package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductStoreInAndOut;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductStoreInAndOutDao extends BaseMongoDao<ProductStoreInAndOut> {
    private static Logger logger = LogManager.getLogger();
    public List<ProductStoreInAndOut> findByProductSeries(ProductSeries productSeries) {
        if (productSeries==null) return null;
        if (productSeries.getProductStore()==null) return null;
        DBObject dbObject=new BasicDBObject();
        DBRef dbRef=new DBRef("productSeries",productSeries.getId());
        dbObject.put("productSeries",dbRef);
        return getMongoTemplate().find(new BasicQuery(dbObject),ProductStoreInAndOut.class);
    }
    public void clearNullUserInAndOut(){
        DBObject dbObject=new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("operator", null));
        values.add(new BasicDBObject("operator", new BasicDBObject("$exists",false)));
        dbObject.put("$or",values);
        dbObject.put("type","out");
        Query query=new BasicQuery(dbObject);
//        System.out.println(query);
        List<ProductStoreInAndOut> inAndOuts=getMongoTemplate().find(query,ProductStoreInAndOut.class);
//        for (ProductStoreInAndOut inAndOut:inAndOuts){
//            if (inAndOut.getProductSeries()!=null) System.out.println(inAndOut.getProductSeries().getName()+":"+inAndOut.getAmount());
//        }
//        getMongoTemplate().remove(new BasicQuery(dbObject));
    }
}
