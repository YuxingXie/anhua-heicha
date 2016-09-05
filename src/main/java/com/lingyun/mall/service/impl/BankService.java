package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Bank;
import com.lingyun.mall.dao.BankDao;
import com.lingyun.mall.service.IBankService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/15.
 */
@Service
public class BankService extends BaseEntityManager<Bank> implements IBankService{
    @Resource
    private BankDao bankDao;
    protected EntityDao<Bank> getEntityDao() {
        return this.bankDao;
    }

}
