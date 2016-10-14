package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.AuthorizeInfo;
import com.lingyun.mall.dao.AuthorizeInfoDao;
import com.lingyun.mall.service.IAuthorizeInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class AuthorizeInfoService extends BaseEntityManager<AuthorizeInfo> implements IAuthorizeInfoService {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private AuthorizeInfoDao authorizeInfoDao;
    protected EntityDao<AuthorizeInfo> getEntityDao() {
        return this.authorizeInfoDao;
    }


}
