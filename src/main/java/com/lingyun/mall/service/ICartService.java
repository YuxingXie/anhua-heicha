package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Cart;
import com.mongodb.DBObject;

/**
 * Created by Administrator on 2015/7/29.
 */
public interface ICartService extends IBaseEntityManager<Cart> {
    Cart getCartByUserId(String id);

    Cart findOne(DBObject condition);
}
