package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Administrator;

/**
 * Created by Administrator on 2015/12/30.
 */
public interface IAdministratorService extends IBaseEntityManager<Administrator> {
    Administrator findByNameAndPassword(String name, String password);
}
