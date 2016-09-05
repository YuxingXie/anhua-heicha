package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.TestAuthors;
import com.lingyun.mall.dao.TestAuthorsDao;
import com.lingyun.mall.service.ITestAuthorsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/11/13.
 */
@Service
public class TestAuthorsService extends BaseEntityManager<TestAuthors> implements ITestAuthorsService {
    @Resource
    private TestAuthorsDao testAuthorsDao;
    protected EntityDao<TestAuthors> getEntityDao() {
        return this.testAuthorsDao;
    }

}
