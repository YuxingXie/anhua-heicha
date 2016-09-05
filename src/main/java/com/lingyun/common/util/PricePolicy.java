package com.lingyun.common.util;

import com.lingyun.entity.SalesCampaign;
import com.lingyun.entity.User;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Administrator on 2015/11/14.
 */
public class PricePolicy {
    public List<SalesCampaign> getSalesCampaignsNow() {
        return salesCampaignsNow;
    }

    public void setSalesCampaignsNow(List<SalesCampaign> salesCampaignsNow) {
        this.salesCampaignsNow = salesCampaignsNow;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public boolean canJoin(SalesCampaign salesCampaign){
        Assert.notNull(salesCampaign);
        if (salesCampaign.getAllProductParticipateIn()!=null&&salesCampaign.getAllProductParticipateIn()) return true;
        if (user==null) return false;
        if (salesCampaignsNow==null) return false;
        if (salesCampaign.getParticipants()==null) return true;//未指定表示都能参加
        if (salesCampaign.getParticipants().contains(user.getUserCategory())){
           return true ;
        }
        return false;
    }

    private User user;
    private List<SalesCampaign> salesCampaignsNow;
}
