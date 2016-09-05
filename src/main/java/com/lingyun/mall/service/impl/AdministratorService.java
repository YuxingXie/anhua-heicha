package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Administrator;
import com.lingyun.mall.dao.AdministratorDao;
import com.lingyun.mall.service.IAdministratorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/30.
 */
@Service
public class AdministratorService  extends BaseEntityManager<Administrator> implements IAdministratorService {
    @Resource
    private AdministratorDao administratorDao;
    protected EntityDao<Administrator> getEntityDao() {
        return this.administratorDao;
    }

    @Override
    public Administrator findByNameAndPassword(String name, String password) {
        return administratorDao.findByNameAndPassword(name,password);
    }
}
