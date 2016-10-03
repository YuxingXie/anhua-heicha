package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.code.UserMeasureSortEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.DateUtil;
import com.lingyun.entity.*;
import com.lingyun.mall.service.impl.UserService;
import com.lingyun.support.vo.Pair;
import com.lingyun.support.yexin.DirectSalePairTouchMode;
import com.lingyun.support.yexin.PairTouchModeMemberRank;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class UserMeasureDao extends BaseMongoDao<UserMeasure> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    @Resource
    private UserService userService;
    @Resource
    private DirectSalePairTouchMode directSalePairTouchMode;


    public List<UserMeasure> findByUser(String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(userId)));
//        Query query=new BasicQuery(dbObject);
//        query.with(new Sort(Sort.Direction.DESC,"date"));
        return findAll(dbObject);
    }
    public List<UserMeasure> findYesterdayPerDayBonusByUser(String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(userId)));
        //昨天的奖励是今天发的，所以日期应是今天
        dbObject.put("date", new BasicDBObject("$ge", DateUtil.getTodayZeroHour()));
        dbObject.put("date", new BasicDBObject("$lt", DateUtil.getToday235959()));
        dbObject.put("isMaterial",false);
        dbObject.put("type",1);
        BasicDBList dbList=new BasicDBList();
        dbList.add(new BasicDBObject("sort",UserMeasureSortEnum.DUIPENG.toCode()));
        dbList.add(new BasicDBObject("sort",UserMeasureSortEnum.JIANDIAN.toCode()));
        dbList.add(new BasicDBObject("sort", UserMeasureSortEnum.ZHITUI.toCode()));
        dbObject.put("$or",dbList);
        return findAll(dbObject);
    }
    public void measureSettlementPerDay() {
        //1.查询昨天成为会员的用户
        DBObject dbObject=new BasicDBObject();
        dbObject.put("directSaleMember",true);
        dbObject.put("becomeMemberDate", new BasicDBObject("$ge", DateUtil.getYesterdayZeroHour()));
        dbObject.put("becomeMemberDate", new BasicDBObject("$lt", DateUtil.getTodayZeroHour()));
//        Query query=new BasicQuery(dbObject);
//        query.with(new Sort(Sort.Direction.ASC,"becomeMemberDate"));
         List<User> newMemberUsers=ServiceManager.userService.findAll(dbObject);
        settleAnyPointBonus(newMemberUsers);
        settleDirectPushBonus(newMemberUsers);
        settleLittleAreaBonus();
    }
    //小区奖励
    private void settleLittleAreaBonus() {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("isDirectSaleMember",true);
        List<User> users=userService.findAll(dbObject);
        if (users==null ||users.size()==0) return;
        List<UserMeasure> littleAreaBonusMeasures=new ArrayList<UserMeasure>();
        Date now=new Date();
        for (User user:users){
            List<User> directLowerUsers=userService.findLowerOrUpperUsers(user, 1);
            if (directLowerUsers==null&&directLowerUsers.size()!=2) continue;
            User first=directLowerUsers.get(0);
            User second=directLowerUsers.get(1);
            List<User> firstAreaUsers=userService.findAllLowerMemberUsers(first);
            List<User> secondAreaUsers=userService.findAllLowerMemberUsers(second);
            if (firstAreaUsers==null ||firstAreaUsers.size()==0) continue;
            if (secondAreaUsers==null|| secondAreaUsers.size()==0) continue;
            List<User> littleAreaUsers=firstAreaUsers.size()>secondAreaUsers.size()?secondAreaUsers:firstAreaUsers;
            double totalAchieve=littleAreaUsers.size()*directSalePairTouchMode.getMembershipLine();
            //根据用户业绩获得用户可以得到的最高小区奖
            PairTouchModeMemberRank userMaxRank=PairTouchModeMemberRank.getRankByAchieve(totalAchieve,directSalePairTouchMode.getMemberRanks());
            if (userMaxRank==null) continue;
            if (userMaxRank.getCashBonus()==0d&&userMaxRank.getMaterialBonus()==null&&userMaxRank.getSalutation()==null) continue;
            PairTouchModeMemberRank userRank=user.getRank();
            int beginOrdinary=userRank==null?1:userRank.getOrdinary()+1;
            inner:for (int i=beginOrdinary;i<=userMaxRank.getOrdinary();i++){
                PairTouchModeMemberRank nextRank=PairTouchModeMemberRank.getRankByOrdinary(beginOrdinary,directSalePairTouchMode.getMemberRanks());
                if (nextRank==null) break inner;
                UserMeasure userMeasure=new UserMeasure();
                userMeasure.setCount(nextRank.getCashBonus());
                StringBuffer note=new StringBuffer("您获得小区业绩奖励");
                if (nextRank.getSalutation()!=null) note.append("，等级晋升为 ").append(nextRank.getSalutation());
                if (nextRank.getCashBonus()!=0d) note.append("，现金奖励 ").append(nextRank.getCashBonus()).append(" 元");
                if (nextRank.getMaterialBonus()!=null) {
                    note.append(",获得 ").append(nextRank.getMaterialBonus());
                    userMeasure.setMaterial(true);
                }
                note.append("。");
                userMeasure.setNote(note.toString());
                userMeasure.setDate(now);
                userMeasure.setSort(UserMeasureSortEnum.XIAOQU.toCode());
                userMeasure.setType(1);
                userMeasure.setUser(user);
                littleAreaBonusMeasures.add(userMeasure);
            }
            user.setRank(userMaxRank);
            userService.update(user);
        }
        insertAll(littleAreaBonusMeasures);

    }

    //直推奖
    private void settleDirectPushBonus(List<User> newMemberUsers) {
        Date now=new Date();
        List<UserMeasure> directPushMeasures=new ArrayList<UserMeasure>();
        List<User> directUpperUsers=userService.getDirectUpperUsers(newMemberUsers);
        for (User directUpperUser :directUpperUsers){
            UserMeasure userMeasure=new UserMeasure();
            double yesterdayBonus=getTotalBonusYesterday(directUpperUser);
            double bonus=directSalePairTouchMode.getPairTouchRate()*directSalePairTouchMode.getMembershipLine();
            if (yesterdayBonus>directSalePairTouchMode.getMaxBonusPerDay()) continue;
            if (yesterdayBonus+bonus>=directSalePairTouchMode.getMaxBonusPerDay()){
                userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                userMeasure.setNote("您获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
            }else{
                userMeasure.setCount(bonus);
                userMeasure.setNote("您获得直推奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
            }
            userMeasure.setDate(now);
            userMeasure.setMaterial(false);
            userMeasure.setSort(UserMeasureSortEnum.ZHITUI.toCode());
            userMeasure.setType(1);
            userMeasure.setUser(directUpperUser);
            directPushMeasures.add(userMeasure);
        }
        insertAll(directPushMeasures);
    }



    //见点奖
    private void settleAnyPointBonus(List<User> newMemberUsers) {
        Date now=new Date();
        int maxLevel=directSalePairTouchMode.getAnyPointFloors();
        List<UserMeasure> anyPointMeasures=new ArrayList<UserMeasure>();
        for (User newMemberUser :newMemberUsers){
            List<User> upperUsers=userService.findLowerOrUpperUsers(newMemberUser,maxLevel*-1);
            if (upperUsers==null ||upperUsers.size()==0) continue;
            for (User upperUser:upperUsers){
                UserMeasure userMeasure=new UserMeasure();
                double yesterdayBonus=getTotalBonusYesterday(upperUser);
                if (yesterdayBonus>directSalePairTouchMode.getMaxBonusPerDay()) continue;
                if (yesterdayBonus+directSalePairTouchMode.getAnyPointBonus()>=directSalePairTouchMode.getMaxBonusPerDay()){
                    userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                    userMeasure.setNote("用户"+newMemberUser.getPhone()+"为您提供见点奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
                }else{
                    userMeasure.setCount(directSalePairTouchMode.getAnyPointBonus());
                    userMeasure.setNote("用户" + newMemberUser.getPhone() + "为您提供见点奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
                }
                userMeasure.setFromUser(newMemberUser);
                userMeasure.setDate(now);
                userMeasure.setMaterial(false);
                userMeasure.setSort(UserMeasureSortEnum.JIANDIAN.toCode());
                userMeasure.setType(1);
                userMeasure.setUser(upperUser);
                anyPointMeasures.add(userMeasure);
            }
        }
        insertAll(anyPointMeasures);
    }
    private double getTotalBonusYesterday(User user) {
        Assert.notNull(user);
        List<UserMeasure> userMeasures= findYesterdayPerDayBonusByUser(user.getId());
        if (userMeasures==null)  return 0;
        double total=0d;
        for (UserMeasure measure:userMeasures){
            total+=measure.getCount();
        }
        return total;
    }

    public static void main(String[] args) {
        String membershipPath="";
        String abcde=membershipPath.substring(0,membershipPath.lastIndexOf("/"));
        String directUpperUserId=abcde.substring(abcde.lastIndexOf("/") + 1);
        System.out.println(membershipPath);
        System.out.println(":"+directUpperUserId);
    }
}
