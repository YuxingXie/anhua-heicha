package com.lingyun.mall.service.impl;

import com.lingyun.common.base.BaseEntityManager;
import com.lingyun.common.base.EntityDao;
import com.lingyun.entity.Order;
import com.lingyun.entity.ProductSeries;
import com.lingyun.mall.dao.OrderDao;
import com.lingyun.mall.service.IOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2015/11/2.
 */
@Service
public class OrderService extends BaseEntityManager<Order> implements IOrderService{
    @Resource
    private OrderDao orderDao;
    protected EntityDao<Order> getEntityDao() {
        return this.orderDao;
    }

    @Override
    public Order findLastOrderByUserId(String userId) {
        return orderDao.findLastOrderByUserId(userId);
    }

    @Override
    public Order findOrderById(String orderId) {
        return orderDao.findOrderById(orderId) ;
    }

    @Override
    public void removeOrderInterval(long ago) {
        orderDao.removeOrderInterval(ago);
    }

    @Override
    public void removeOrderById(String id) {
        orderDao.removeOrderById(id);
    }

    @Override
    public long findUnHandlerOrdersCount() {
        return orderDao.findUnHandlerOrdersCount();
    }

    @Override
    public List<Order> findUnHandlerOrders() {
        return orderDao.findUnHandlerOrders();
    }

    @Override
    public List<Order> findOrdersByProductSeries(ProductSeries productSeries) {
        return orderDao.findOrdersByProductSeries(productSeries);
    }

    @Override
    public long findOrdersCountByProductSeries(ProductSeries productSeries) {
        return orderDao.findOrdersCountByProductSeries(productSeries);
    }

    @Override
    public List<Order> findReturnExchangeOrders() {
        return orderDao.findReturnExchangeOrders();
    }

    @Override
    public long findReturnExchangeOrdersCount() {
        return orderDao.findReturnExchangeOrdersCount();
    }

}
