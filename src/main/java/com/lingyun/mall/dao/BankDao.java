package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.Bank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/12/15.
 */
@Repository
public class BankDao  extends BaseMongoDao<Bank> {
    private static Logger logger = LogManager.getLogger();
}
