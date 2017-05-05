package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.AlipayTrans;
import com.lingyun.entity.User;

import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IAlipayTransService extends IBaseEntityManager<AlipayTrans> {
    List<AlipayTrans> findSubmittedTransByUser(User user);
    double findSubmittedTransTotalFeeByUser(User user);
    List<AlipayTrans> findSubmittedAndNotSendToAlipayTrans();

    List<AlipayTrans> findAlipayTransFinished();

}
