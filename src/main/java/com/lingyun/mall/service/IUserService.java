package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Order;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.User;
import com.lingyun.entity.UserPoints;
import com.lingyun.support.vo.Message;

import java.util.List;

/**
 * Created by Administrator on 2015/11/6.
 */
public interface IUserService extends IBaseEntityManager<User> {
    User findByEmailOrPhone(String name);
    public void updateUserAfterOrder(Order order);
    void clearCart(User user);
    List<User> findUpperUsers(User user, int maxRelativeLevel);

    List<User> findUsersByProductSeriesInCart(ProductSeries productSeries);

    User findInviteUserByPhoneAndInviteCode(String phone, String inviteCode);
    List<User> getDirectUpperUsers(List<User> newMemberUsers);
    void insertUser(User user);
    List<User> findAllLowerUsers(User user);
    long findAllLowerUsersCount(User user);
    List<User> findAllLowerMemberUsers(User user);
    long findAllLowerMemberUsersCount(User user);
    /**
     * 获得用户的上级或下级用户
     * @param user
     * @param maxRelativeLevel 最大关系层数，获取下级用户为正数，上级为负数,比如获得上级最大为9级的用户列表时，maxRelativeLevel值为-9
     * @return
     */
    List<User> findLowerOrUpperUsers(User user,int maxRelativeLevel);

    User findByEmailOrPhoneAndPassword(String loginStr,String password);

    List<UserPoints> findUserPointsByUser(String userId);

    User findDirectUpperUser(User memberUser);

    User getDirectUpperUser(User membershipUser);

    Message isValidUpper(String upperPhone);
    User findByPhone(String phone);

    User findFirstMember();

    List<User> findAllMembers();

    User findBrotherUser(User user);

    boolean isInLittleAreaWhenRegister(User newMemberUser, User upperUser);
}
