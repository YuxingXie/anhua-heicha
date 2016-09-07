package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Bank;
import com.lingyun.entity.FriendshipMall;
import com.lingyun.mall.dao.BankDao;
import com.lingyun.mall.dao.FriendshipMallDao;
import com.lingyun.mall.service.IBankService;
import com.lingyun.mall.service.IFriendshipMallService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/15.
 */
@Service
public class FriendshipMallService extends BaseEntityManager<FriendshipMall> implements IFriendshipMallService{
    @Resource
    private FriendshipMallDao friendshipMallDao;
    protected EntityDao<FriendshipMall> getEntityDao() {
        return this.friendshipMallDao;
    }

}
