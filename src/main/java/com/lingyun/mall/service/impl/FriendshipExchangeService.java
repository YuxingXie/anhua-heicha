package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.FriendshipExchange;
import com.lingyun.mall.dao.FriendshipExchangeDao;
import com.lingyun.mall.service.IFriendshipExchangeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/15.
 */
@Service
public class FriendshipExchangeService extends BaseEntityManager<FriendshipExchange> implements IFriendshipExchangeService{
    @Resource
    private FriendshipExchangeDao friendshipExchangeDao;
    protected EntityDao<FriendshipExchange> getEntityDao() {
        return this.friendshipExchangeDao;
    }

}
