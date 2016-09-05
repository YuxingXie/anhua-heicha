package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.TopCarousel;
import com.lingyun.mall.dao.TopCarouselDao;
import com.lingyun.mall.service.ITopCarouselService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/12/31.
 */
@Service
public class TopCarouselService extends BaseEntityManager<TopCarousel> implements ITopCarouselService {
    @Resource
    private TopCarouselDao topCarouselDao;
    protected EntityDao<TopCarousel> getEntityDao() {
        return this.topCarouselDao;
    }

    @Override
    public TopCarousel findByMaxPriority() {
        return topCarouselDao.findByMaxPriority();
    }
}
