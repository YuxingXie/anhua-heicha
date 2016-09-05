package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.Notify;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/12/21.
 */
@Repository
public class NotifyDao extends BaseMongoDao<Notify> {
    private static Logger logger = LogManager.getLogger();
}
