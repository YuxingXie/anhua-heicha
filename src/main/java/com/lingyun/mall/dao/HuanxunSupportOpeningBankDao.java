package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.HuanxunSupportBank;
import com.lingyun.entity.HuanxunSupportOpeningBank;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class HuanxunSupportOpeningBankDao extends BaseMongoDao<HuanxunSupportOpeningBank> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;

    public boolean openingBankExists(String openingBank) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("bankName",openingBank);
        long count=mongoTemplate.count(new BasicQuery(dbObject),HuanxunSupportOpeningBank.class);
        return count>0;
    }
}
