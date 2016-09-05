package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.Interest;
import com.lingyun.entity.ProductSeries;
import com.lingyun.entity.User;

import java.util.List;

/**
 * Created by Administrator on 2015/11/26.
 */
public interface  IInterestService extends IBaseEntityManager<Interest> {
    List<Interest> findInterestsOfUser(User user);

    boolean alreadyInterested(User user, ProductSeries productSeries);

    List<Interest> findByUserAndProductSeries(User user, ProductSeries productSeries);

    List<Interest> findByProductSeries(ProductSeries productSeries);
}
