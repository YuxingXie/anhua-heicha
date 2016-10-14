package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.Notify;
import com.lingyun.entity.User;
import com.lingyun.entity.UserPoints;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class UserPointsDao extends BaseMongoDao<UserPoints> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    public void addPointsToAllUser(int points) {
        logger.info("add points to all users");
        DBObject dbObject=new BasicDBObject();
        dbObject.put("directSaleMember", true);
        //db.mallUser.update({},{"$set":{"directSaleMember":true}},false,true)
        Query query=new BasicQuery(dbObject);
        List<User> users=getMongoTemplate().find(query, User.class);
        Date now=new Date();
        List<Notify> notifies=new ArrayList<Notify>();
        for (User user:users){
            UserPoints userPoints=new UserPoints();
            userPoints.setCount(points);
            userPoints.setDate(now);
            userPoints.setUser(user);
            userPoints.setNote("系统每日赠送");
            userPoints.setType(1);
            insert(userPoints);

            Notify notify=new Notify();
            notify.setContent("系统每日赠送您 " + points + " 点红包。");
            notify.setTitle("系统通知");
            notify.setDate(now);
            notify.setToUser(user);
            notify.setNotifyType("SYSTEM");
            notifies.add(notify);
        }
        if (notifies.size()>0)
            ServiceManager.notifyService.insertAll(notifies);

    }
}
