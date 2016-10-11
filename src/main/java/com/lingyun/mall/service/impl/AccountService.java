package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Account;
import com.lingyun.entity.User;
import com.lingyun.mall.dao.AccountDao;
import com.lingyun.mall.service.IAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class AccountService extends BaseEntityManager<Account> implements IAccountService{
    private static Logger logger = LogManager.getLogger();
    @Resource
    private AccountDao accountDao;
    protected EntityDao<Account> getEntityDao() {
        return this.accountDao;
    }

    @Override
    public Account findAccountsByUserIdAndCardNo(String userId, String cardNo) {
        return accountDao.findAccountsByUserId(userId,cardNo);
    }

    @Override
    public List<Account> findAccountsByUser(User user) {
        return accountDao.findAccountsByUser( user);
    }
}
