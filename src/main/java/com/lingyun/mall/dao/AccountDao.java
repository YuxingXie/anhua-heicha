package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.Account;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class AccountDao extends BaseMongoDao<Account> {
    private static Logger logger = LogManager.getLogger();
    public Account findAccountsByUserId(String userId, String cardNo) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("userId",userId);
        dbObject.put("cardNo",cardNo);
        return getMongoTemplate().findOne(new BasicQuery(dbObject),Account.class);
    }
}
