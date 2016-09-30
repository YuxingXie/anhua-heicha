package com.lingyun.support.yexin;

/**
 * Created by Administrator on 2016/9/25.
 */
public class PairTouchModeMemberRank {
    private double zoneMin;
    private double zoneMax;
    private String salutation;
    private double cashBonus;
    private String materialBonus;

    public PairTouchModeMemberRank(double zoneMin, double zoneMax, String salutation, double cashBonus,String materialBonus) {
        this.zoneMin = zoneMin;
        this.zoneMax = zoneMax;
        this.salutation = salutation;
        this.cashBonus = cashBonus;
        this.materialBonus=materialBonus;
    }

    public double getZoneMin() {
        return zoneMin;
    }

    public double getZoneMax() {
        return zoneMax;
    }

    public String getSalutation() {
        return salutation;
    }

    public double getCashBonus() {
        return cashBonus;
    }

    public String getMaterialBonus() {
        return materialBonus;
    }
}
