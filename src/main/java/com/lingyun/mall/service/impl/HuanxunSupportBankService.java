package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Account;
import com.lingyun.entity.HuanxunSupportBank;
import com.lingyun.entity.User;
import com.lingyun.mall.dao.AccountDao;
import com.lingyun.mall.dao.HuanxunSupportBankDao;
import com.lingyun.mall.service.IAccountService;
import com.lingyun.mall.service.IHuanxunSupportBankService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class HuanxunSupportBankService extends BaseEntityManager<HuanxunSupportBank> implements IHuanxunSupportBankService{
    private static Logger logger = LogManager.getLogger();
    @Resource
    private HuanxunSupportBankDao huanxunSupportBankDao;
    protected EntityDao<HuanxunSupportBank> getEntityDao() {
        return this.huanxunSupportBankDao;
    }


}
