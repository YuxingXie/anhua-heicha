package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.*;
import com.lingyun.support.mapReduce.ProductPriceMR;
import com.lingyun.support.vo.Sortable;
import com.mongodb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/6/11.
 */
@Repository
public class ProductSeriesDao extends BaseMongoDao<ProductSeries> {
    private static Logger logger = LogManager.getLogger();
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
    public List<String[]> getTop3ProductSeries() {
        List<String[]> list=new ArrayList<String[]>();
        String[] product1={"11111122228x1","statics/assets/temp/sliders/slide2/bg.jpg","黄材水产","新品上市","中华鲟","优惠价","25"};
        String[] product2={"0x1451112ffb","statics/assets/temp/sliders/slide5/bg.jpg","本地","生态腊鱼","货源充足，放心购买"};
        String[] product3={"4512f44d003f","statics/assets/temp/sliders/slide3/bg.jpg","正宗乡里腊肉","源自宁乡","您购买的不二选择","最新优惠价","149"};
        list.add(product1);
        list.add(product2);
        list.add(product3);
        return list;
    }
    public List<String[]> getTop3ProductSeriesDemo() {

        List<String[]> list=new ArrayList<String[]>();
        String[] product1={"11111122228x1","statics/assets/temp/sliders/slide2/bg.jpg","第一个产品","广告词2","广告词3","优惠价","25"};
        String[] product2={"0x1451112ffb","statics/assets/temp/sliders/slide5/bg.jpg","这是","第2个产品","我是第二个产品的广告词"};
        String[] product3={"4512f44d003f","statics/assets/temp/sliders/slide3/bg.jpg","第三个产品","广告词。。","这里八个字左右为好","五个字左右","149"};
        list.add(product1);
        list.add(product2);
        list.add(product3);
        return list;
    }

    public List<ProductSeries> getAll() {
        return getAll(null);
    }
    public List<ProductSeries> getAll(Integer limit) {
        List<ProductSeries> productSeriesList=findAll(limit);
        getStoresAndPrices(productSeriesList);
        return productSeriesList;
    }
    public void getStoresAndPrices(List<ProductSeries> productSeriesList) {
//        for (ProductSeries productSeries:productSeriesList){
//            getStoreAndPrice(productSeries);
//        }
    }
    public void getEvaluates(List<ProductSeries> productSeriesList) {
        for (ProductSeries productSeries:productSeriesList){
//            getStoreAndPrice(productSeries);
            List<ProductEvaluate> productEvaluateList=ServiceManager.productEvaluateService.findAll(new BasicDBObject("productSeries",productSeries));
            productSeries.setProductEvaluateList(productEvaluateList);
        }
    }

    public ProductSeries findProductSeriesByIdButEvaluate(ObjectId objectId,boolean ignoreEvaluate) {

        ProductSeries productSeries=findById(objectId);
        DBObject productPropertyDbObject=new BasicDBObject();
        productPropertyDbObject.put("productSeries", new DBRef("productSeries", objectId));
        List<ProductProperty> productProperties= ServiceManager.productPropertyService.findAll(productPropertyDbObject);
        for (ProductProperty productProperty :productProperties){
            DBObject cond=new BasicDBObject();
            cond.put("productProperty", new DBRef("productProperty",productProperty.getId()));
            List<ProductPropertyValue> propertyValues=ServiceManager.productPropertyValueService.findAll(cond);
            productProperty.setPropertyValues(propertyValues);
        }
        productSeries.setProductProperties(productProperties);
       if (!ignoreEvaluate){
           List<ProductEvaluate> productEvaluateList=ServiceManager.productEvaluateService.findAll(new BasicDBObject("productSeries",new DBRef("productSeries",objectId)));
           productSeries.setProductEvaluateList(productEvaluateList);
       }
        return productSeries;
    }
    public ProductSeries findProductSeriesById(ObjectId objectId) {
        return findProductSeriesByIdButEvaluate(objectId,false);
    }
    public ProductSeries findProductSeriesById(String id) {
        return findProductSeriesById(new ObjectId(id));
    }
    public Page<ProductSeries> findProductSeriesPageByKeyWord(String keyWord, int currentPage, int pageSize) {
//        Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(".*?" + keyWord + ".*"), Criteria.where("description").regex(".*?" + keyWord + ".*"));
        Pattern pattern = Pattern.compile(".*?" + keyWord + ".*");
        DBObject queryCondition=new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("name", pattern));
        values.add(new BasicDBObject("description", pattern));
//        values.add(new BasicDBObject("productSubCategory.subCategoryName", pattern));
//        values.add(new BasicDBObject("productSubCategory.productCategory.categoryName", pattern));
        queryCondition.put("$or", values);
        DB db = getMongoTemplate().getDb();
        DBCollection collection = db.getCollection("productSeries");
        Pageable pageable = new PageRequest(currentPage-1, pageSize);
        Long count = collection.count(queryCondition);
        List<ProductSeries> list = getMongoTemplate().find(new BasicQuery(queryCondition).limit(pageSize).skip((currentPage - 1) * pageSize), ProductSeries.class);
        getEvaluates(list);
        Page<ProductSeries> page = new PageImpl<ProductSeries>(list, pageable, count);
//        if (count>0)
        return page;


    }
    public List<ProductSeries> findProductSeriesByName(String name) {
        Pattern pattern = Pattern.compile(".*?" + name + ".*");
        DBObject queryCondition=new BasicDBObject("name", pattern);
        DB db = getMongoTemplate().getDb();
        List<ProductSeries> list = getMongoTemplate().find(new BasicQuery(queryCondition), ProductSeries.class);
        return list;
    }
    public Page<ProductSeries> findProductSeriesPageByProductSubCategory(ProductSubCategory productSubCategory,int currentPage,int pageSize) {
        return findProductSeriesPageByProductSubCategoryWithSort(productSubCategory, currentPage, pageSize, null);
    }

    public Page<ProductSeries> findProductSeriesPageByProductSubCategoryWithSort(ProductSubCategory productSubCategory, int currentPage, int pageSize, Sortable sortable) {
        DBObject queryCondition=new BasicDBObject();
        queryCondition.put("productSubCategory",productSubCategory);
        Pageable pageable = new PageRequest(currentPage-1, pageSize);
        Long count = getMongoTemplate().count(new BasicQuery(queryCondition), ProductSeries.class);
        Query q=null;
        if (sortable!=null){
            Sort.Direction direction=sortable.getAsc()? Sort.Direction.ASC: Sort.Direction.DESC;
            String field=new String();
            if (sortable.getField().equals("sales"))  field="sales";
            else if (sortable.getField().equals("evaluate")) field="evaluateCount";
            else field=sortable.getField();
            q=new BasicQuery(queryCondition).with(new Sort(direction,field)).limit(pageSize).skip((currentPage - 1) * pageSize);
        }else {
            q=new BasicQuery(queryCondition).limit(pageSize).skip((currentPage - 1) * pageSize);;
        }
        List<ProductSeries> list = getMongoTemplate().find(q, ProductSeries.class);
        getEvaluates(list);
        Page<ProductSeries> page = new PageImpl<ProductSeries>(list, pageable, count);
        return page;


    }
    public Page<ProductSeries> findProductSeriesPageByProductSubCategorySortByPrice(ProductSubCategory productSubCategory, int currentPage, int pageSize, Sortable sortable) {
        Query mrQuery=new BasicQuery(new BasicDBObject("productSubCategory",new DBRef("productSubCategory",new ObjectId(productSubCategory.getId()))));
        MapReduceResults<ProductSeries> mapReduceResults=
                getMongoTemplate().mapReduce(mrQuery,ProductPriceMR.inputCollectionName, ProductPriceMR.mapFun, ProductPriceMR.reduceFun,ProductPriceMR.mapReduceOptions(), ProductSeries.class);
        DBObject dbObject=new BasicDBObject();
        Query query=new BasicQuery(dbObject);
        Pageable pageable = new PageRequest(currentPage-1, pageSize);
        Long count = getMongoTemplate().count(query, ProductPriceMR.class);
        Sort.Direction direction=sortable.getAsc()? Sort.Direction.ASC: Sort.Direction.DESC;
        String field="value.price";
        List<ProductPriceMR> list = getMongoTemplate().find(query.with(new Sort(direction,field)).limit(pageSize).skip((currentPage - 1) * pageSize), ProductPriceMR.class);
        List<ProductSeries> retList=new ArrayList<ProductSeries>();
        for (ProductPriceMR mr:list){
            retList.add(mr.getValue().getProductSeries());
        }
        getEvaluates(retList);
        Page<ProductSeries> page = new PageImpl<ProductSeries>(retList, pageable, count);
        return page;
    }
    public List<ProductSeries> findProductSeriesAllRef(DBObject dbObject) {
            List<ProductSeries> list = getMongoTemplate().find(new BasicQuery(dbObject), ProductSeries.class);
         getEvaluates(list);
        return list;
    }

    public List<ProductSeries> getLowPrices(int count) {
        return null;
    }

    public List<ProductSeries> getNewProducts(int count) {
        DBObject dbObject=new BasicDBObject();
        List<ProductSeries> productSeriesList=getMongoTemplate().find(new BasicQuery(dbObject).with(new Sort(Sort.Direction.DESC,"shelvesDate")).limit(count), ProductSeries.class);
        getStoresAndPrices(productSeriesList);
        return productSeriesList;
    }

    public List<ProductSeries> getHotSell(int count) {
        //可规定热卖商品数量

        return getAll(count);
    }

    public Page<ProductSeries> orderByPrice(ProductSubCategory subCategory,int currentPage,int pageSize){
        //1:投射
        AggregationOperation project=Aggregation.project("productSeries","price");
        AggregationOperation match = Aggregation.match(Criteria.where("service").is("EFT").and("source").is("MARKUP"));
        AggregationOperation group = Aggregation.group("card_acceptor").sum("amount").as("amount_sum").count().as("tran_count");
        AggregationOperation sort = Aggregation.sort(new Sort(Sort.Direction.ASC,"price"));
        Aggregation aggregation = Aggregation.newAggregation(match,project, group,sort);
        AggregationResults<ProductSeries> result = getMongoTemplate().aggregate(aggregation, "eft_transactions", ProductSeries.class);
        return null;
    }


    public List<ProductSeries> findProductSeriesByProductCategory(ProductCategory productCategory) {
        List<ProductSubCategory>subCategories=ServiceManager.productSubCategoryService.getProductSubCategoriesByCategoryId(productCategory.getId());
        DBObject dbObject=new BasicDBObject();

        dbObject.put("productSubCategory",new BasicDBObject("$in",subCategories));
        return getMongoTemplate().find(new BasicQuery(dbObject),ProductSeries.class);
    }

    public List<ProductSeries> findProductSeriesByProductSubCategory(ProductSubCategory productSubCategory) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("productSubCategory",new DBRef("productSubCategory",new ObjectId(productSubCategory.getId())));
        return getMongoTemplate().find(new BasicQuery(dbObject),ProductSeries.class);
    }


    public void removeProductSeriesAndPictures(ProductSeries productSeries) {
        List<ProductSeriesPicture> productSeriesPictures=productSeries.getPictures();
        if(productSeriesPictures!=null){
            for (ProductSeriesPicture productSeriesPicture:productSeriesPictures){
                String bigPicture=productSeriesPicture.getBigPicture();
                String picture=productSeriesPicture.getPicture();
                String iconPicture=productSeriesPicture.getIconPicture();
                String bigPictureId=bigPicture.substring(4);
                String pictureId=picture.substring(4);
                String iconPictureId=iconPicture.substring(4);
                deleteFile(bigPictureId);
                deleteFile(pictureId);
                deleteFile(iconPictureId);
            }
        }

        removeById(productSeries.getId());
    }
}
