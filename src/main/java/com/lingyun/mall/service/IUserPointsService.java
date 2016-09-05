package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Account;
import com.lingyun.entity.UserPoints;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IUserPointsService extends IBaseEntityManager<UserPoints> {
    void addPointsToAllUser(int points);
}
