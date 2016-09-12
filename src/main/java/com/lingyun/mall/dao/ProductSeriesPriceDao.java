package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.ProductSeriesPrice;
import com.lingyun.entity.ProductSubCategory;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/11/16.
 */
@Repository
public class ProductSeriesPriceDao  extends BaseMongoDao<ProductSeriesPrice> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    private DBObject getCurrentPriceDBObject(){
        DBObject dbObject=new BasicDBObject();
//        Date now=new Date();
        long now=System.currentTimeMillis();
        dbObject.put("beginDate",new BasicDBObject("$exists",true));
        dbObject.put("beginDate",new BasicDBObject("$lt",now));
        BasicDBList endDateDbList=new BasicDBList();
        endDateDbList.add(new BasicDBObject("endDate",new BasicDBObject("$gt",now)));
        endDateDbList.add(new BasicDBObject("endDate", new BasicDBObject("$exists", false)));
        dbObject.put("$or",endDateDbList);
        return dbObject;
    }
    private Criteria getCurrentPriceCriteria(){
        Date now=new Date();
        Criteria criteria= Criteria.where("beginDate").exists(true).lt(now).orOperator(Criteria.where("endDate").gt(now), Criteria.where("endDate").exists(false));
        logger.trace(criteria.getCriteriaObject());
        return  criteria;
    }
    public static void main(String[] args){
        new ProductSeriesPriceDao().getCurrentPriceCriteria();
    }
    public List<ProductSeriesPrice> findByProductSeriesId(String id) {
        DBRef dbRef=new DBRef("productSeries",new ObjectId(id));
        Query query=new BasicQuery(new BasicDBObject("productSeries",dbRef));
        return getMongoTemplate().find(query,ProductSeriesPrice.class);

    }
    public Page<ProductSeries> getProductSeriesOrderByPriceInProductSubCategory(ProductSubCategory productSubCategory,Integer currentPage,Integer pageSize,boolean asc){
        /*Sort.Direction direction=asc?Sort.Direction.ASC:Sort.Direction.DESC;
        DBObject queryCondition=getCurrentPriceDBObject();
//        queryCondition.put("productSeries.productSubCategory",new DBRef("productSubCategory",productSubCategory.getId()));
        Pageable pageable = new PageRequest(currentPage-1, pageSize);
        Long count = getMongoTemplate().count(new BasicQuery(queryCondition), ProductSeriesPrice.class);
        Query q=new BasicQuery(queryCondition).with(new Sort(direction, "price")).limit(pageSize).skip((currentPage - 1) * pageSize);
        System.out.println(q);
        List<ProductSeriesPrice> productSeriesPriceList = getMongoTemplate().find(q, ProductSeriesPrice.class);
        List<ProductSeries> list=new ArrayList<ProductSeries>();
        for (ProductSeriesPrice productSeriesPrice:productSeriesPriceList){
            list.add(productSeriesPrice.getProductSeries());
        }
        Page<ProductSeries> page = new PageImpl<ProductSeries>(list, pageable, count);
        return page;*/
        //1:投射
        AggregationOperation project= Aggregation.project("pid=$productSeries.$id", "pppp=$price");
//        AggregationOperation project= Aggregation.project("productSeries.$id");
        AggregationOperation match = Aggregation.match(getCurrentPriceCriteria());
//        AggregationOperation group = Aggregation.group("card_acceptor").sum("amount").as("amount_sum").count().as("tran_count");
        AggregationOperation sort = Aggregation.sort(new Sort(Sort.Direction.ASC,"price"));

        Aggregation aggregation = Aggregation.newAggregation(match,project,sort);
        AggregationResults<LinkedHashMap> result = getMongoTemplate().aggregate(aggregation, "productSeriesPrice", LinkedHashMap.class);

        return null;
    }
}
