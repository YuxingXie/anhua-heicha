package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.User;
import com.lingyun.entity.UserMeasure;
import com.lingyun.mall.dao.TestUser;
import com.lingyun.mall.dao.UserMeasureDao;
import com.lingyun.mall.service.IUserMeasureService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public List<UserMeasure> findByUser(String userId) {
        return userMeasureDao.findByUser(userId);
    }

    @Override
    public void measureSettlementPerDay() {
        userMeasureDao.measureSettlementPerDay();
    }

    @Override
    public int testMode(int level, int levelUserCount,int totalUserCount, Map<Integer, Integer> levelUserCountMap, Map<Integer, Integer> totalUserCountMap, Map<Integer, List<TestUser>> levelUsersMap) {
        return userMeasureDao.testMaxLevel(level,levelUserCount,totalUserCount,levelUserCountMap,totalUserCountMap,levelUsersMap);
    }

    @Override
    public List<UserMeasure> findIncomeByUser(User user) {
        return userMeasureDao.findIncomeByUser(user);
    }

    @Override
    public List<UserMeasure> findByUser(User user) {
        return userMeasureDao.findByUser(user);
    }
}
