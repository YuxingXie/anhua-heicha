package com.lingyun.entity;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class ProductStore{
    @Transient
    private int remain;
    private Integer warningAmount;
//    @Transient
    @Field
    private List<ProductStoreInAndOut> inAndOutList;

    public int getRemain() {
        if (inAndOutList==null) return 0;
        if (inAndOutList.size()==0) return 0;
        Integer remain=0;
        for (ProductStoreInAndOut inAndOut :inAndOutList){
            String type=inAndOut.getType();
            if(type==null) continue;
            if(inAndOut.getAmount()==null) continue;
            if (type.equals("in"))
            remain+=inAndOut.getAmount();
            else remain-=inAndOut.getAmount();
        }
        this.remain=remain;
        return this.remain;
    }

    public Integer getWarningAmount() {
        return warningAmount;
    }

    public void setWarningAmount(Integer warningAmount) {
        this.warningAmount = warningAmount;
    }

    public List<ProductStoreInAndOut> getInAndOutList() {
        return inAndOutList;
    }

    public void setInAndOutList(List<ProductStoreInAndOut> inAndOutList) {
        this.inAndOutList = inAndOutList;
    }

}
