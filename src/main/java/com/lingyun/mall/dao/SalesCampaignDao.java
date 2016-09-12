package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSeriesRate;
import com.lingyun.entity.SalesCampaign;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/7.
 */
@Repository
public class SalesCampaignDao extends BaseMongoDao<SalesCampaign> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    public void removeProductSeries(ProductSeries productSeries) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("productSeriesRates.productSeries",new DBRef("productSeries",new ObjectId(productSeries.getId())));
        List<SalesCampaign> salesCampaignList=getMongoTemplate().find(new BasicQuery(dbObject),SalesCampaign.class);
        for (SalesCampaign salesCampaign:salesCampaignList){
            List<ProductSeriesRate> newRates=new ArrayList<ProductSeriesRate>();
            for(ProductSeriesRate rate:salesCampaign.getProductSeriesRates()){
                ProductSeries rateProductSeries=rate.getProductSeries();
                if (!rateProductSeries.getId().equalsIgnoreCase(productSeries.getId())){
                    newRates.add(rate);
                }
            }
            SalesCampaign updateSalesCampaign=new SalesCampaign();
            updateSalesCampaign.setId(salesCampaign.getId());
            updateSalesCampaign.setProductSeriesRates(newRates);
            upsert(updateSalesCampaign);
        }
    }
}
