package com.lingyun.support.vo;

import com.lingyun.entity.Order;
import com.lingyun.entity.ReturnExchange;

/**
 * Created by Administrator on 2016/1/11.
 */
public class OrderReturnExchange {
    private Order order;
    private ReturnExchange returnExchange;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ReturnExchange getReturnExchange() {
        return returnExchange;
    }

    public void setReturnExchange(ReturnExchange returnExchange) {
        this.returnExchange = returnExchange;
    }
}
