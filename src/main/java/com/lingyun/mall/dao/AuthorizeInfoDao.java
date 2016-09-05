package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.Account;
import com.lingyun.entity.AuthorizeInfo;
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
public class AuthorizeInfoDao extends BaseMongoDao<AuthorizeInfo> {
    private static Logger logger = LogManager.getLogger();

}
