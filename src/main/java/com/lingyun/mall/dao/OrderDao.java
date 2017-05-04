package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.*;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository
public class OrderDao extends BaseMongoDao<Order> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    public Order findLastOrderByUserId(String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("userId",userId);
        Query query=new BasicQuery(dbObject).with(new Sort(Sort.Direction.DESC, "orderDate"));
        Order order= getMongoTemplate().findOne(query,Order.class);
        getDBRefs(order);
        return order;
    }

    public Order findOrderById(String orderId) {
        Order order=findById(orderId);
        getDBRefs(order);
        return order;
    }

    public void getDBRefs(Order order) {
        if (order==null) return;
        List<ProductSelected> productSelectedList=order.getProductSelectedList();
//        List<ProductSeries> productSeriesList=new ArrayList<ProductSeries>();
        if (productSelectedList!=null){
            for (ProductSelected productSelected:productSelectedList){
                ProductSeries productSeries= productSelected.getProductSeries();
//
               ProductEvaluate productEvaluate= ServiceManager.productEvaluateService.findByOrderAndProductSeries(order, productSeries);
               productSelected.setProductEvaluate(productEvaluate);
//                productSeries.setProductSeriesPrices(ServiceManager.productSeriesPriceService.findByProductSeriesId(productSeries.getId()));
                List<String> productPropertyValueIds=productSelected.getProductPropertyValueIds();
                if (productPropertyValueIds!=null){
                    List<ProductPropertyValue> productPropertyValueList=new ArrayList<ProductPropertyValue>();
                    for (String productPropertyValueId:productPropertyValueIds){
                        ProductPropertyValue productPropertyValue=ServiceManager.productPropertyValueService.findById(productPropertyValueId);
                        productPropertyValueList.add(productPropertyValue);
                    }
                    productSelected.setProductPropertyValueList(productPropertyValueList);
                }
//                if (productSeries.getProductStore()!=null){
//                    List<ProductStoreInAndOut> inAndOuts=ServiceManager.productStoreInAndOutService.findByProductSeries(productSeries);
//                    productSeries.getProductStore().setInAndOutList(inAndOuts);
//                }
            }
        }
    }

    public void removeOrderInterval(long ago) {
        DBObject dbObject=new BasicDBObject();
        Calendar agoC=new GregorianCalendar();
        agoC.setTimeInMillis(System.currentTimeMillis() - ago);
//        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ", Locale.CHINA);
        dbObject.put("orderDate", new BasicDBObject("$lt", agoC.getTime()));
        dbObject.put("orderDate",new BasicDBObject("$lt",agoC.getTime()));
        dbObject.put("payStatus",new BasicDBObject("$exists",false));


        Query query=new BasicQuery(dbObject);
        List<Order> orders=getMongoTemplate().find(query, Order.class);
        for (Order order:orders){
            DBObject evalDBObject=new BasicDBObject();
            evalDBObject.put("order",new DBRef("order",order.getId()));
            List<ProductEvaluate> evaluates=ServiceManager.productEvaluateService.findAll(evalDBObject);
            if (evaluates!=null &&evaluates.size()>0) continue;
            if (order.getUser()==null) continue;
            Notify notify=new Notify();
            notify.setContent("您的订单 "+order.getId()+" 因超过7天未使用，系统已自动删除。");
            Map<String,Object> importantStuff=new HashMap<String, Object>();
            importantStuff.put("订单商品",order.getProductSelectedList());
            notify.setTitle("系统通知");
            notify.setImportantStuffs(importantStuff);
            notify.setDate(new Date());
            notify.setToUser(order.getUser());
            notify.setNotifyType("SYSTEM");
            ServiceManager.notifyService.insert(notify);
            getMongoTemplate().remove(new BasicQuery(new BasicDBObject("_id",order.getId())), Order.class);
        }

//        System.out.println("清除了" + orders.size() + "条订单");
    }

    public void removeOrderById(String id) {
        DBObject evaluateDBObject=new BasicDBObject();
        evaluateDBObject.put("order",new DBRef("order",id));
        mongoTemplate.findAllAndRemove(new BasicQuery(evaluateDBObject), ProductEvaluate.class);
        removeById(id);
    }

    public long findUnHandlerOrdersCount() {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("payStatus","y");
        BasicDBList dbList=new BasicDBList();
        dbList.add(new BasicDBObject("handler",false));
        dbList.add(new BasicDBObject("handler",new BasicDBObject("$exists",false)));
        dbObject.put("$or",dbList);
        return getMongoTemplate().count(new BasicQuery(dbObject),Order.class);
    }

    public List<Order> findUnHandlerOrders() {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("payStatus","y");
        BasicDBList dbList=new BasicDBList();
        dbList.add(new BasicDBObject("handler",false));
        dbList.add(new BasicDBObject("handler",new BasicDBObject("$exists",false)));
        dbObject.put("$or",dbList);
        return getMongoTemplate().find(new BasicQuery(dbObject),Order.class);
    }

    public List<Order> findOrdersByProductSeries(ProductSeries productSeries) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("productSelectedList.productSeries",productSeries);
        return getMongoTemplate().find(new BasicQuery(dbObject),Order.class);
    }

    public long findOrdersCountByProductSeries(ProductSeries productSeries) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("productSelectedList.productSeries",productSeries);
        return getMongoTemplate().count(new BasicQuery(dbObject),Order.class);
    }

    public List<Order> findReturnExchangeOrders() {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("payStatus","y");
        dbObject.put("productSelectedList.returnExchangeList",new BasicDBObject("$exists",true));
        BasicDBList dbList=new BasicDBList();
        dbList.add(new BasicDBObject("productSelectedList.returnExchangeList",new BasicDBObject("$elemMatch",new BasicDBObject("handler",false))));
        dbList.add(new BasicDBObject("productSelectedList.returnExchangeList",new BasicDBObject("$elemMatch",new BasicDBObject("handler",new BasicDBObject("$exists",false)))));
//        dbList.add(new BasicDBObject("productSelectedList.returnExchangeList.$.handler",new BasicDBObject("$exists",false)));
        dbObject.put("$or",dbList);
//        System.out.println(new BasicQuery(dbObject));
        return getMongoTemplate().find(new BasicQuery(dbObject),Order.class);
    }

    public long findReturnExchangeOrdersCount() {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("payStatus","y");
        dbObject.put("productSelectedList.returnExchangeList",new BasicDBObject("$exists",true));
        BasicDBList dbList=new BasicDBList();
        dbList.add(new BasicDBObject("productSelectedList.returnExchangeList",new BasicDBObject("$elemMatch",new BasicDBObject("handler",false))));
        dbList.add(new BasicDBObject("productSelectedList.returnExchangeList",new BasicDBObject("$elemMatch",new BasicDBObject("handler",new BasicDBObject("$exists",false)))));
//        dbList.add(new BasicDBObject("productSelectedList.returnExchangeList.$.handler",new BasicDBObject("$exists",false)));
        dbObject.put("$or",dbList);
//        System.out.println(new BasicQuery(dbObject));
        return mongoTemplate.count(new BasicQuery(dbObject),Order.class);
    }

    public List<Order> findOrdersPaidByUserId(String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(userId)));
        dbObject.put("payStatus","y");
        return findAll(dbObject);
    }
}
