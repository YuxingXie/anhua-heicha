package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Account;
import com.lingyun.entity.User;

import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IAccountService extends IBaseEntityManager<Account> {
    Account findAccountsByUserIdAndCardNo(String userId, String cardNo);
    List<Account> findAccountsByUser(User user);
}
