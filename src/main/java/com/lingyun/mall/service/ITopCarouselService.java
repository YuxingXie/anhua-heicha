package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.TopCarousel;

/**
 * Created by Administrator on 2015/12/31.
 */
public interface ITopCarouselService extends IBaseEntityManager<TopCarousel> {
    TopCarousel findByMaxPriority();
}
