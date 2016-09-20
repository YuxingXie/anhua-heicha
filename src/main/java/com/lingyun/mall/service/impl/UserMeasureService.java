package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Order;
import com.lingyun.entity.UserMeasure;
import com.lingyun.entity.UserPoints;
import com.lingyun.mall.dao.UserMeasureDao;
import com.lingyun.mall.dao.UserPointsDao;
import com.lingyun.mall.service.IUserMeasureService;
import com.lingyun.mall.service.IUserPointsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class UserMeasureService extends BaseEntityManager<UserMeasure> implements IUserMeasureService{
    private static Logger logger = LogManager.getLogger();
    @Resource
    private UserMeasureDao userMeasureDao;
    protected EntityDao<UserMeasure> getEntityDao() {
        return this.userMeasureDao;
    }

    @Override
    public void sendMeasureToUpperUser(Order order) {
        userMeasureDao.sendMeasureToUpperUser(order);
    }

    @Override
    public List<UserMeasure> findByUser(String userId) {
        return userMeasureDao.findByUser(userId);
    }
}
