package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Order;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.User;
import com.lingyun.entity.UserPoints;
import com.lingyun.mall.dao.UserDao;
import com.lingyun.mall.service.IUserService;
import com.lingyun.support.vo.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/6.
 */
@Service
public class UserService extends BaseEntityManager<User> implements IUserService {
    @Resource
    private UserDao userDao;
    public User findByEmailOrPhone(String emailOrPhone){
        return userDao.findByEmailOrPhone(emailOrPhone);
    }

    @Override
    public void updateUserAfterOrder(Order order) {
        userDao.updateUserAfterOrder(order);
    }

    @Override
    public void clearCart(User user) {
        userDao.clearCart(user);
    }

    @Override
    public List<User> findUpperUsers(User user, int maxRelativeLevel) {
        return userDao.findUpperUsers(user, maxRelativeLevel);
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
    public List<User> getDirectUpperUsers(List<User> newMemberUsers) {
        return userDao.getDirectUpperUsers(newMemberUsers);
    }

    @Override
    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public List<User> findAllLowerUsers(User user) {
        return userDao.findAllLowerUsers(user);
    }
    @Override
    public long findAllLowerUsersCount(User user) {
        return userDao.findAllLowerUsersCount(user);
    }

    @Override
    public List<User> findAllLowerMemberUsers(User user) {
        return userDao.findAllLowerMemberUsers(user);
    }

    @Override
    public long findAllLowerMemberUsersCount(User user) {
        return userDao.findAllLowerMemberUsersCount(user);
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

    @Override
    public User findDirectUpperUser(User memberUser) {
        return userDao.findDirectUpperUser(memberUser);
    }
    @Override
    public User findGrandpaUser(User memberUser) {
        return userDao.findGrandpaUser(memberUser);
    }
    @Override
    public User getDirectUpperUser(User membershipUser) {
        return userDao.getDirectUpperUser(membershipUser);
    }

    @Override
    public Message isValidUpper(String upperPhone) {
        return userDao.isValidUpper(upperPhone);
    }

    @Override
    public User findByPhone(String phone) {
        return userDao.findByPhone(phone);
    }

    @Override
    public User findFirstMember() {
        return userDao.findFirstMember();
    }

    @Override
    public List<User> findAllMembers() {
        return userDao.findAllMembers();
    }

    @Override
    public User findBrotherUser(User user) {
        return userDao.findBrotherUser(user);
    }

    @Override
    public boolean isInLittleAreaWhenRegister(User newMemberUser, User upperUser) {
        return userDao.isInLittleAreaWhenRegister(newMemberUser,upperUser);
    }
}
