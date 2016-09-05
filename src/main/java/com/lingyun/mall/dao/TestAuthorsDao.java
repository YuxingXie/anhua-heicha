package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.TestAuthors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class TestAuthorsDao  extends BaseMongoDao<TestAuthors> {
    private static Logger logger = LogManager.getLogger();
}
