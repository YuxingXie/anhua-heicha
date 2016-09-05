package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Notify;
import com.lingyun.mall.dao.NotifyDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/21.
 */
@Service
public class NotifyService extends BaseEntityManager<Notify> implements INotifyService {
    @Resource
    private NotifyDao notifyDao;
    protected EntityDao<Notify> getEntityDao() {
        return this.notifyDao;
    }
}
