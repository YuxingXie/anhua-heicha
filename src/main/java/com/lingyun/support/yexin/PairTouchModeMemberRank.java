package com.lingyun.support.yexin;

import java.util.List;

/**
 * Created by Administrator on 2016/9/25.
 */
public class PairTouchModeMemberRank implements Comparable<PairTouchModeMemberRank> {
    private double zoneMin;
    private double zoneMax;
    private String salutation;
    private double cashBonus;
    private String materialBonus;
    /**
     * order必须设置为从1开始的连续自然数
     */
    private int ordinary;

    public PairTouchModeMemberRank(double zoneMin, double zoneMax, String salutation, double cashBonus,String materialBonus,int ordinary) {
        this.zoneMin = zoneMin;
        this.zoneMax = zoneMax;
        this.salutation = salutation;
        this.cashBonus = cashBonus;
        this.materialBonus=materialBonus;
        this.ordinary=ordinary;
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

    public int getOrdinary() {
        return ordinary;
    }

    public static PairTouchModeMemberRank getRankByAchieve(double totalAchieve, List<PairTouchModeMemberRank> memberRanks) {
        for (PairTouchModeMemberRank rank:memberRanks){
            if(rank.getZoneMax()==0d){
                if (totalAchieve>=rank.getZoneMin()){
                    return rank;
                }
            }else{
                if (totalAchieve>=rank.getZoneMin()&&totalAchieve<rank.getZoneMax()){
                    return rank;
                }
            }
        }
        return null;
    }
    public static PairTouchModeMemberRank getRankByOrdinary(int ordinary, List<PairTouchModeMemberRank> memberRanks) {
        for (PairTouchModeMemberRank rank:memberRanks){
            if(rank.ordinary==ordinary){
                    return rank;
            }
        }
        return null;
    }

    @Override
    public int compareTo(PairTouchModeMemberRank o) {
        return this.ordinary-o.ordinary;
    }
}
