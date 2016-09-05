package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.TestPosts;
import com.lingyun.mall.dao.TestPostsDao;
import com.lingyun.mall.service.ITestPostsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestPostsService extends BaseEntityManager<TestPosts> implements ITestPostsService {
    @Resource
    private TestPostsDao testPostsDao;
    protected EntityDao<TestPosts> getEntityDao() {
        return this.testPostsDao;
    }

    @Override
    public void insertDBRef(TestPosts testPosts) {
        testPostsDao.insertDBRef(testPosts);
    }
}
