package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Order;
import com.lingyun.entity.UserMeasure;
import com.lingyun.entity.UserPoints;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IUserMeasureService extends IBaseEntityManager<UserMeasure> {
    void sendMeasureToUpperUser(Order order);
}
