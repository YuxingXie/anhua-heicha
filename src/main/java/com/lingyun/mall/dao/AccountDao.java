package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.Account;
import com.lingyun.entity.User;
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
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class AccountDao extends BaseMongoDao<Account> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    public Account findAccountsByUserId(String userId, String cardNo) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("userId",userId);
        dbObject.put("cardNo",cardNo);
        return getMongoTemplate().findOne(new BasicQuery(dbObject),Account.class);
    }

    public List<Account> findAccountsByUser(User user) {
        if (user==null) return null;
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(user.getId())));
        return findAll(dbObject);
    }
}
