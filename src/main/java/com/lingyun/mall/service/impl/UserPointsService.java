package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.UserPoints;
import com.lingyun.mall.dao.UserPointsDao;
import com.lingyun.mall.service.IUserPointsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class UserPointsService extends BaseEntityManager<UserPoints> implements IUserPointsService{
    private static Logger logger = LogManager.getLogger();
    @Resource
    private UserPointsDao userPointsDao;
    protected EntityDao<UserPoints> getEntityDao() {
        return this.userPointsDao;
    }


    @Override
    public void addPointsToAllUser(int points) {
        userPointsDao.addPointsToAllUser(points);
    }
}
