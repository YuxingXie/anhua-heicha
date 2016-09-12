package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.Administrator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


@Repository
public class AdministratorDao  extends BaseMongoDao<Administrator> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    public Administrator findByNameAndPassword(String name, String password) {

        DBObject dbObject=new BasicDBObject();
        dbObject.put("name",name);
        dbObject.put("password",password);
        return getMongoTemplate().findOne(new BasicQuery(dbObject),Administrator.class);
    }
}
