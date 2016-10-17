package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.User;
import com.lingyun.entity.UserMeasure;
import com.lingyun.mall.dao.TestUser;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IUserMeasureService extends IBaseEntityManager<UserMeasure> {


    List<UserMeasure> findByUser(String userId);

    void measureSettlementPerDay();

    int testMode(int level,int levelUserCount, int totalUserCount,Map<Integer, Integer> levelUserCountMap, Map<Integer, Integer> totalUserCountMap, Map<Integer, List<TestUser>> levelUsersMap);

    List<UserMeasure> findIncomeByUser(User user);

    List<UserMeasure> findByUser(User user);
}
