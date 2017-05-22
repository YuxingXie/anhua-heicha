package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.HuanxunSupportBank;
import com.lingyun.entity.HuanxunSupportOpeningBank;
import com.lingyun.mall.dao.HuanxunSupportBankDao;
import com.lingyun.mall.dao.HuanxunSupportOpeningBankDao;
import com.lingyun.mall.service.IHuanxunSupportBankService;
import com.lingyun.mall.service.IHuanxunSupportOpeningBankService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class HuanxunSupportOpeningBankService extends BaseEntityManager<HuanxunSupportOpeningBank> implements IHuanxunSupportOpeningBankService{
    private static Logger logger = LogManager.getLogger();
    @Resource
    private HuanxunSupportOpeningBankDao huanxunSupportOpeningBankDao;
    protected EntityDao<HuanxunSupportOpeningBank> getEntityDao() {
        return this.huanxunSupportOpeningBankDao;
    }


    @Override
    public boolean openingBankExists(String openingBank) {
        return huanxunSupportOpeningBankDao.openingBankExists(openingBank);
    }
}
