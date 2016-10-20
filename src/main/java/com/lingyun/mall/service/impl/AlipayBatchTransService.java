package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.AlipayBatchTrans;
import com.lingyun.entity.AlipayTrans;
import com.lingyun.entity.User;
import com.lingyun.mall.dao.AlipayBatchTransDao;
import com.lingyun.mall.dao.AlipayTransDao;
import com.lingyun.mall.service.IAlipayBatchTransService;
import com.lingyun.mall.service.IAlipayTransService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class AlipayBatchTransService extends BaseEntityManager<AlipayBatchTrans> implements IAlipayBatchTransService {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private AlipayBatchTransDao alipayBatchTransDao;
    protected EntityDao<AlipayBatchTrans> getEntityDao() {
        return this.alipayBatchTransDao;
    }


}
