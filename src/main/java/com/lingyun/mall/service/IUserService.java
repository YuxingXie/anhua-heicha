package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.User;
import com.lingyun.entity.UserPoints;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Administrator on 2015/11/6.
 */
public interface IUserService extends IBaseEntityManager<User> {
    User findByEmailOrPhone(String name);

    void clearCart(User user);

    List<User> findUsersByProductSeriesInCart(ProductSeries productSeries);

    User findInviteUserByPhoneAndInviteCode(String phone, String inviteCode);

    void insertUser(User user);

    List<User> findLowerUsers(User user);

    User findByEmailOrPhoneAndPassword(String loginStr,String password);

    List<UserPoints> findUserPointsByUser(String userId);
}
