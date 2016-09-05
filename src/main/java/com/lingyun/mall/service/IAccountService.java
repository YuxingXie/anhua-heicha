package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Account;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IAccountService extends IBaseEntityManager<Account> {
    Account findAccountsByUserIdAndCardNo(String userId, String cardNo);
}
