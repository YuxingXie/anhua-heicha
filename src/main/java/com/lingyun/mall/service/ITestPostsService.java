package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.TestPosts;

public interface ITestPostsService extends IBaseEntityManager<TestPosts> {
    void insertDBRef(TestPosts testPosts);
}
