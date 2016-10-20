package com.lingyun.mall.dao;


import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.code.AlipayTransStatusEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.Account;
import com.lingyun.entity.AlipayBatchTrans;
import com.lingyun.entity.AlipayTrans;
import com.lingyun.entity.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class AlipayBatchTransDao extends BaseMongoDao<AlipayBatchTrans> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;


}
