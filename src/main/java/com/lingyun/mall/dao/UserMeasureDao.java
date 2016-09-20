package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.entity.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class UserMeasureDao extends BaseMongoDao<UserMeasure> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    @Resource
    private UserDao userDao;
    public void sendMeasureToUpperUser(Order order) {
        if (order==null) return;
        User user=order.getUser();
        if (user==null) return;
        List<User> upperUsers=userDao.findLowerOrUpperUsers(user,-9);
        System.out.println("user's upper user count is:"+upperUsers==null?0:upperUsers.size());
        Date now=new Date();
        List<UserMeasure> measures=new ArrayList<UserMeasure>();
        for (User upperUser:upperUsers){
            logger.info("---------------------");
            logger.info("first upper user");
            UserRelationship userRelationship=new UserRelationship(user,upperUser);
            double rate= userRelationship.getRate();
            logger.info("    rate is :" + rate);
            if (rate==0d) {
                logger.info("    rate is 0,to be continue");
                continue;
            }
            double count= rate*order.getTotalPrice();
            logger.info("    order total price is zero,to be continue");
            if (count==0d){
               logger.info("    measure count is 0 ,to be continue");
                continue;
            }
            UserMeasure measure=new UserMeasure();
            measure.setNote(userRelationship.getRelativeLevel() * -1 + "级用户 " + user.getShowName() + " 为您提供了 " + BigDecimalUtil.multiply(count * 1) + " 元财富");
//            logger.info(userRelationship.getRelativeLevel() * -1 + "级用户 " + user.getShowName() + " 为您提供了 " + BigDecimalUtil.multiply(count * 1) + " 元财富");
            measure.setDate(now);
            measure.setCount(BigDecimalUtil.multiply(count * 1));
            measure.setFromUser(user);
            measure.setUser(upperUser);
            measure.setType(1);
            measures.add(measure);
        }
        mongoTemplate.insertAll(measures);
//        System.out.println("insert all:"+measures.size());
    }

    public List<UserMeasure> findByUser(String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(userId)));
        return findAll(dbObject);
    }
}
