package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Cart;
import com.lingyun.mall.dao.CartDao;
import com.lingyun.mall.service.ICartService;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/7/29.
 */
@Service
public class CartService extends BaseEntityManager<Cart> implements ICartService {
    @Resource
    private CartDao cartDao;
    protected EntityDao<Cart> getEntityDao() {
        return this.cartDao;
    }

    @Override
    public Cart getCartByUserId(String id) {
        return cartDao.getCartByUserId(id);
    }

    @Override
    public Cart findOne(DBObject condition) {
        return cartDao.findOne(condition);
    }
}
