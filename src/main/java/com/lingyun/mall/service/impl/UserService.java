package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.User;
import com.lingyun.entity.UserPoints;
import com.lingyun.mall.dao.UserDao;
import com.lingyun.mall.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Administrator on 2015/11/6.
 */
@Service
public class UserService extends BaseEntityManager<User> implements IUserService {
    @Resource
    private UserDao userDao;
    public User findByEmailOrPhone(String name){
        return userDao.findByEmailOrPhone(name);
    }

    @Override
    public void clearCart(User user) {
        userDao.clearCart(user);
    }

    @Override
    public List<User> findUsersByProductSeriesInCart(ProductSeries productSeries) {
        return userDao.findUsersByProductSeriesInCart(productSeries);
    }



    protected EntityDao<User> getEntityDao() {
        return this.userDao;
    }

    public User findByTencentOpenId(String openId) {
        return userDao.findByTencentOpenId(openId);
    }

    public User findInviteUserByPhoneAndInviteCode(String phone, String inviteCode) {
        return userDao.findInviteUserByPhoneAndInviteCode(phone, inviteCode) ;
    }

    @Override
    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public List<User> findLowerOrUpperUsers(User user,int maxRelativeLevel) {
        return userDao.findLowerOrUpperUsers(user,maxRelativeLevel);
    }

    @Override
    public User findByEmailOrPhoneAndPassword(String loginStr, String password) {
        return userDao.findByEmailOrPhoneAndPassword(loginStr, password) ;
    }

    @Override
    public List<UserPoints> findUserPointsByUser(String userId) {
        return userDao.findUserPointsByUser(userId);
    }
}
