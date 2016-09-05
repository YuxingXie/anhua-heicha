package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.ProductPropertyValue;
import com.lingyun.mall.dao.ProductPropertyValueDao;
import com.lingyun.mall.service.IProductPropertyValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/10/24.
 */
@Service
public class ProductPropertyValueService extends BaseEntityManager<ProductPropertyValue> implements IProductPropertyValueService {
    @Resource
    private ProductPropertyValueDao productPropertyValueDao;
    protected EntityDao<ProductPropertyValue> getEntityDao() {
        return this.productPropertyValueDao;
    }

}
