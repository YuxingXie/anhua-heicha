package com.lingyun.support.yexin;

import com.lingyun.common.directSale.mode.AbstractDirectSaleMode;
import com.lingyun.common.util.BigDecimalUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public  class DirectSalePairTouchMode extends AbstractDirectSaleMode {
    private double anyPointRate;
    private double anyPointBonus;
    private int anyPointFloors;
    private double pairTouchRate;
    private double membershipLine;
    private double maxBonusPerDay;
    private List<PairTouchModeMemberRank> memberRanks;
    private static Logger logger = LogManager.getLogger();
    public DirectSalePairTouchMode(double anyPointRate,double anyPointBonus, int anyPointFloors,double pairTouchRate,double membershipLine,double maxBonusPerDay, List<PairTouchModeMemberRank> memberRanks) {
        this.anyPointRate = anyPointRate;
        this.anyPointBonus=anyPointBonus;
        this.anyPointFloors=anyPointFloors;
        this.pairTouchRate = pairTouchRate;
        this.membershipLine=membershipLine;
        this.maxBonusPerDay=maxBonusPerDay;
        this.memberRanks = memberRanks;
        logger.info("config yexin direct sale mode finished.");
    }

    public double getAnyPointRate() {
        return anyPointRate;
    }

    public double getPairTouchRate() {
        return pairTouchRate;
    }

    public List<PairTouchModeMemberRank> getMemberRanks() {
        return memberRanks;
    }

    public double getMaxBonusPerDay() {
        return maxBonusPerDay;
    }

    public double getMembershipLine() {
        return membershipLine;
    }

    public double getAnyPointBonus() {
        return anyPointBonus;
    }

    public int getAnyPointFloors() {
        return anyPointFloors;
    }

    public PairTouchModeMemberRank getRank(double performance){
        if (performance<0) return null;
        if (memberRanks==null) return null;
        for (PairTouchModeMemberRank rank:memberRanks){
            if (rank.getZoneMax()==0){
                if(performance>=rank.getZoneMin()){
                    return rank;
                }
            }else{
                if(performance>=rank.getZoneMin() &&performance<rank.getZoneMax()){
                    return rank;
                }
            }
        }
        return null;
    }
    public static void main(String[] args){
        double totalMoney=0d;
        int userCount=1;
        int levelUserCount=1;
        double out=0d;
        double in=0d;
        int level=1;
        double price=10000d;
        double anyPointBonus=800d;
        double directPushBonusRate=0.1d;
        double pairTouchBonusRate=0.15d;
        boolean get1bonus=false;
        boolean get2bonus=false;
        boolean get3bonus=false;
        System.out.println("\n\n");
        System.out.println("##############################################################");
        System.out.println("见点奖"+anyPointBonus+",直推奖:"+directPushBonusRate+"*"+price+"="+(directPushBonusRate*price)
                +",对碰奖"+pairTouchBonusRate+"*"+price+"="+(pairTouchBonusRate*price));
        System.out.println("##############################################################");
        System.out.println("---------------------------------------------------------------------------------------");
        while(totalMoney>=0d){
            String rank="";
            double rankBonus=0d;
            double levelIn=0d;
            double levelOut=0d;
            double levelAnyPointBonus=0d;
            double levelDirectPushBonus=0d;
            double levelPairTouchBonus=0d;
            level++;
            levelUserCount*=2;
            userCount+=levelUserCount;
            //见点奖  只拿15层
            if (level<15){
                levelAnyPointBonus=anyPointBonus*(level-1)*levelUserCount;
                levelOut+=levelAnyPointBonus;
            }else{

                levelAnyPointBonus=anyPointBonus*14*levelUserCount;
                levelOut+=levelAnyPointBonus;
            }
            //直推奖
//            out+=levelUserCount*price*directPushBonusRate;
            levelDirectPushBonus=levelUserCount*price*directPushBonusRate;
            levelOut+=levelDirectPushBonus;

//            in+=price*levelUserCount;
            levelIn+=price*levelUserCount;

            //对碰奖

            levelPairTouchBonus=levelUserCount/2*price*pairTouchBonusRate;
            levelOut+=levelPairTouchBonus;

            if(in/2>120000 &&in/2 <=300000 &&!get1bonus){
                rankBonus=1500d;
                levelOut+=rankBonus;
                get1bonus=true;
                rank="业务主任奖";

            }
            if(in/2>300000 &&in/2 <=1000000&& !get2bonus){
                rank="业务经理奖(价值2500华为手机)";
                rankBonus=2500d;
                levelOut+=rankBonus;//華為手機價格
                get2bonus=true;
            }
            if(in/2>1000000 &&!get3bonus){
                rank="高级业务经理奖";
                rankBonus=30000d;
                levelOut+=rankBonus;
                get3bonus=true;
            }
            in+=levelIn;
            out+=levelOut;
            totalMoney+=in-out;

            BigDecimal bd = new BigDecimal(totalMoney);
            bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal inbd = new BigDecimal(levelIn);
            inbd.setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal outbd = new BigDecimal(levelOut);
            outbd.setScale(2, BigDecimal.ROUND_HALF_UP);
            System.out.println(level + "层,总用户数：" + userCount  + ",新增用户:" + levelUserCount + " ,in:" + inbd.doubleValue() + ",out:" + outbd.doubleValue()+ "，余额:" + bd.doubleValue());
            if (rankBonus>0d)
                System.out.println("    其中：见点奖："+levelAnyPointBonus+",直推奖:"+levelDirectPushBonus+" ,"+rank+": "+rankBonus+",对碰奖："+levelPairTouchBonus);
            else
                System.out.println("    其中：见点奖："+levelAnyPointBonus+",直推奖:"+levelDirectPushBonus+" ,对碰奖："+levelPairTouchBonus);
            System.out.println("---------------------------------------------------------------------------------------");
        }
        System.out.println("level "+level);
        BigDecimal bd = new BigDecimal(totalMoney);
        bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println("totalMoney "+ bd.doubleValue());
        System.out.println("end ");
    }
}
