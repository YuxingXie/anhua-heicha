package com.lingyun.mall.service;

import com.lingyun.common.base.IBaseEntityManager;
import com.lingyun.entity.HuanxunSupportBank;
import com.lingyun.entity.HuanxunSupportOpeningBank;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface IHuanxunSupportOpeningBankService extends IBaseEntityManager<HuanxunSupportOpeningBank> {
    boolean openingBankExists(String openingBank);
}
