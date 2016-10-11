package com.lingyun.mall.dao;

/**
 * Created by Administrator on 2016/10/7.
 */
public class TestUser{
    private double bonus;
    private  int level;
    private int orderInLevel;
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public int getOrderInLevel() {
        return orderInLevel;
    }
    public void setOrderInLevel(int orderInLevel) {
        this.orderInLevel = orderInLevel;
    }
    public double getBonus() {
        return bonus;
    }
    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}
