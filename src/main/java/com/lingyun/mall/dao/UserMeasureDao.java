package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.code.NotifyTypeCodeEnum;
import com.lingyun.common.code.UserMeasureSortEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.DateUtil;
import com.lingyun.entity.Notify;
import com.lingyun.entity.User;
import com.lingyun.entity.UserMeasure;
import com.lingyun.mall.service.impl.UserService;
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
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.awt.geom.Arc2D;
import java.util.*;

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
        dbObject.put("date", new BasicDBObject("$gt", DateUtil.getTodayZeroHour()));
        dbObject.put("date", new BasicDBObject("$lt", DateUtil.getToday235959()));
//        dbObject.put("isMaterial",false);
        dbObject.put("type",1);
        BasicDBList dbList=new BasicDBList();
        dbList.add(new BasicDBObject("sort",UserMeasureSortEnum.DUIPENG.toCode()));
        dbList.add(new BasicDBObject("sort",UserMeasureSortEnum.JIANDIAN.toCode()));
        dbList.add(new BasicDBObject("sort", UserMeasureSortEnum.ZHITUI.toCode()));
        dbObject.put("$or",dbList);
        return findAll(dbObject);
    }


    //佣金每日结算
    public void measureSettlementPerDay() {
        //1.查询昨天成为会员的用户
        DBObject dbObject=new BasicDBObject();
        dbObject.put("directSaleMember",true);
        DBObject dateBetween=new BasicDBObject();
//        long nowMillis=System.currentTimeMillis();
//        long oneDayAgoMillis=nowMillis-1000*60*60*24;
        dateBetween.put("$gte", DateUtil.getYesterdayZeroHour());
//        dateBetween.put("$gte", new Date(oneDayAgoMillis));
        dateBetween.put("$lt", DateUtil.getTodayZeroHour());
//        dateBetween.put("$lt", new Date(nowMillis));
        dbObject.put("becomeMemberDate", dateBetween);
         List<User> newMemberUsers=ServiceManager.userService.findAll(dbObject);
        System.out.println(new BasicQuery(dbObject));
        int num=newMemberUsers==null?0:newMemberUsers.size();
        logger.info("昨日注册用户数："+num);
        if (num==0) return;
//        settleAnyPointBonus(newMemberUsers);
        settleDirectPushBonus(newMemberUsers);
        settlePairTouchBonus(newMemberUsers);
        settleLittleAreaBonus();
    }
    //对碰奖
    private void settlePairTouchBonus(List<User> newMemberUsers) {
        logger.info("发放对碰奖");
        Date now=new Date();
        for (User newMemberUser:newMemberUsers){
            User upperUser=userService.findDirectUpperUser(newMemberUser);
            double newMembershipUserCost=newMemberUser.getCost();
            int upperLevel=0;
            while (upperUser!=null){
                upperLevel++;
                if (upperLevel==1) {
                    upperUser=userService.findDirectUpperUser(upperUser);
                    continue;
                }
                boolean inLittleArea=userService.isInLittleAreaWhenRegister(newMemberUser,upperUser);
                UserMeasure userMeasure=new UserMeasure();
                double yesterdayBonus=getTotalBonusYesterday(upperUser);
                if (inLittleArea){
                    logger.info(upperUser.getPhone()+":昨日奖励:"+yesterdayBonus);
                    if (yesterdayBonus>=directSalePairTouchMode.getMaxBonusPerDay()) {
                        logger.info(upperUser.getPhone() + ":昨日奖励已达最大，不再发放奖励");
                        continue;
                    }
                    if (yesterdayBonus+directSalePairTouchMode.getPairTouchRate()*(newMembershipUserCost-800)>=directSalePairTouchMode.getMaxBonusPerDay()){
                        userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                        userMeasure.setNote("您获得对碰奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
                        logger.info("用户 "+upperUser.getPhone()+" 获得对碰奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
                    }else{
                        userMeasure.setCount(directSalePairTouchMode.getPairTouchRate()*(newMembershipUserCost-800));
                        userMeasure.setNote("您获得对碰奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
                        logger.info("用户 "+upperUser.getPhone()+" 获得对碰奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元。");
                    }
                    logger.info(upperUser.getPhone() + ":" + userMeasure.getNote());
                    userMeasure.setDate(now);
                    userMeasure.setSort(UserMeasureSortEnum.DUIPENG.toCode());
                    userMeasure.setType(1);
                    userMeasure.setUser(upperUser);
                    insert(userMeasure);

                }
                upperUser=userService.findDirectUpperUser(upperUser);
            }
        }


    }



    //直推奖
    private void settleDirectPushBonus(List<User> newMemberUsers) {
        logger.info("发放直推奖");
        Date now=new Date();
        List<UserMeasure> measures=new ArrayList<UserMeasure>();
        List<Notify> notifies=new ArrayList<Notify>();
        for (User newMembershipUser :newMemberUsers){
            Notify notify=new Notify();
            notify.setDate(now);
            notify.setNotifyType(NotifyTypeCodeEnum.SYSTEM.toCode());
            notify.setTitle("系统消息");
            double newMembershipUserCost=newMembershipUser.getCost();
            User directUpperUser=userService.getDirectUpperUser(newMembershipUser);
            notify.setToUser(directUpperUser);
            if (directUpperUser==null) continue;
            if (directUpperUser.getId().equals("58d545a9a7e9bc113c6fc28d")) {
                System.out.printf("test point");
            }
            UserMeasure userMeasure=new UserMeasure();
            double yesterdayBonus=getTotalBonusYesterday(directUpperUser);
            double bigBonus=directSalePairTouchMode.getDirectPushRateMarketBig()*(newMembershipUserCost-800);
            double littleBonus=directSalePairTouchMode.getDirectPushRateMarketLittle()*(newMembershipUserCost-800);
            if (yesterdayBonus>=directSalePairTouchMode.getMaxBonusPerDay()) continue;
            User brotherUser=userService.findBrotherUser(newMembershipUser);
            if (brotherUser==null){
                if (yesterdayBonus+littleBonus>=directSalePairTouchMode.getMaxBonusPerDay()){
                    userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                    userMeasure.setNote("您获得直推奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元,您的每日奖励已达封顶。");
                    logger.info("用户 " + directUpperUser.getPhone() + " 获得直推奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元,每日奖励已达封顶。");
                }else{
                    userMeasure.setCount(littleBonus);
                    userMeasure.setNote("您获得直推奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
                    logger.info("用户 "+directUpperUser.getPhone()+" 获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元。");
                }
                logger.info("   奖励来自用户"+newMembershipUser.getPhone()+",该用户没有兄弟用户，因此获取奖励 "+directSalePairTouchMode.getDirectPushRateMarketLittle());
            }else{
                if (brotherUser.getBecomeMemberDate()==null){

//                    notify.setContent("为您发放直推奖励时出现一个错误，系统无法确定用户 "+brotherUser.getPhone()+" 的注册时间");

//                    ServiceManager.notifyService.insert(notify);
//                    return;
                }
                if (brotherUser.getBecomeMemberDate()==null||newMembershipUser.getBecomeMemberDate().before(brotherUser.getBecomeMemberDate())){//第一个用户10%
                    if (yesterdayBonus+littleBonus>=directSalePairTouchMode.getMaxBonusPerDay()){
                        userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                        userMeasure.setNote("您获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
                        logger.info("用户 "+directUpperUser.getPhone()+" 获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,每日奖励已达封顶。");

                    }else{
                        userMeasure.setCount(littleBonus);
                        userMeasure.setNote("您获得直推奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
                        logger.info("用户 "+directUpperUser.getPhone()+" 获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元。");
                    }
                    logger.info("   奖励来自用户"+newMembershipUser.getPhone()+",该用户的弟弟用户为 "+brotherUser.getPhone()+
                            "弟弟用户的注册时间为"+brotherUser.getBecomeMemberDate()+" ，因此获取奖励 "+directSalePairTouchMode.getDirectPushRateMarketLittle());
                }else{
                    if (yesterdayBonus+bigBonus>=directSalePairTouchMode.getMaxBonusPerDay()){
                        userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                        userMeasure.setNote("您获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
                        logger.info("用户 "+directUpperUser.getPhone()+" 获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,每日奖励已达封顶。");
                    }else{
                        userMeasure.setCount(bigBonus);
                        userMeasure.setNote("您获得直推奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
                        logger.info("用户 "+directUpperUser.getPhone()+" 获得直推奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元。");
                    }
                    logger.info("   奖励来自用户"+newMembershipUser.getPhone()+",该用户的哥哥用户为 "+brotherUser.getPhone()+
                            "哥哥用户的注册时间为"+brotherUser.getBecomeMemberDate()+" ，因此获取奖励 "+directSalePairTouchMode.getDirectPushRateMarketBig());
                }

            }
            userMeasure.setDate(now);
            userMeasure.setMaterial(false);
            userMeasure.setSort(UserMeasureSortEnum.ZHITUI.toCode());
            userMeasure.setType(1);
            userMeasure.setUser(directUpperUser);
            measures.add(userMeasure);
            notify.setContent(userMeasure.getNote());
            notifies.add(notify);
//            insert(userMeasure);
        }
        if (measures.size()>0)
            insertAll(measures);
        if (notifies.size()>0)
            ServiceManager.notifyService.insertAll(notifies);
    }



    //见点奖
    private void settleAnyPointBonus(List<User> newMemberUsers) {
        logger.info("发放见点奖");
        Date now=new Date();
        int maxLevel=directSalePairTouchMode.getAnyPointFloors();
        for (User newMemberUser :newMemberUsers){
            List<User> upperUsers=userService.findLowerOrUpperUsers(newMemberUser,maxLevel*-1);
            if (upperUsers==null ||upperUsers.size()==0) continue;
            for (User upperUser:upperUsers){
                UserMeasure userMeasure=new UserMeasure();
                double yesterdayBonus=getTotalBonusYesterday(upperUser);
                logger.info(upperUser.getPhone()+":昨日奖励:"+yesterdayBonus);
                if (yesterdayBonus>=directSalePairTouchMode.getMaxBonusPerDay()) {
                    logger.info(upperUser.getPhone() + ":昨日奖励已达最大");
                    continue;
                }
                if (yesterdayBonus+directSalePairTouchMode.getAnyPointBonus()>=directSalePairTouchMode.getMaxBonusPerDay()){
                    userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                    userMeasure.setNote("用户"+newMemberUser.getPhone()+"为您提供见点奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
                    logger.info("用户 "+upperUser.getPhone()+" 获得见点奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,每日奖励已达封顶。");
                }else{
                    userMeasure.setCount(directSalePairTouchMode.getAnyPointBonus());
                    userMeasure.setNote("用户" + newMemberUser.getPhone() + "为您提供见点奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
                    logger.info("用户 "+upperUser.getPhone()+" 获得见点奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元。");
                }
                logger.info(upperUser.getPhone() + ":" + userMeasure.getNote());
                userMeasure.setFromUser(newMemberUser);
                userMeasure.setDate(now);
                userMeasure.setMaterial(false);
                userMeasure.setSort(UserMeasureSortEnum.JIANDIAN.toCode());
                userMeasure.setType(1);
                userMeasure.setUser(upperUser);
                insert(userMeasure);
            }
        }
    }
    //小区奖励
    private void settleLittleAreaBonus() {
        logger.info("发放小区奖励");
        DBObject dbObject=new BasicDBObject();
        dbObject.put("directSaleMember",true);
        List<User> users=userService.findAll(dbObject);
        if (users==null ||users.size()==0) return;
        List<UserMeasure> littleAreaBonusMeasures=new ArrayList<UserMeasure>();
        Date now=new Date();
        for (User user:users){
            List<User> directLowerUsers=userService.findLowerOrUpperUsers(user, 1);
            if (directLowerUsers==null||directLowerUsers.size()!=2) continue;
            User first=directLowerUsers.get(0);
            User second=directLowerUsers.get(1);
            List<User> firstAreaUsers=userService.findAllLowerMemberUsers(first);
            List<User> secondAreaUsers=userService.findAllLowerMemberUsers(second);
            if (firstAreaUsers==null ||firstAreaUsers.size()==0) continue;
            if (secondAreaUsers==null|| secondAreaUsers.size()==0) continue;
            List<User> littleAreaUsers=firstAreaUsers.size()>secondAreaUsers.size()?secondAreaUsers:firstAreaUsers;
            double totalAchieve=littleAreaUsers.size()*directSalePairTouchMode.getMembershipLine();
            PairTouchModeMemberRank userRank= findUserRank(user);
            //根据用户业绩获得用户可以得到的最高小区奖
            PairTouchModeMemberRank userMaxRank=PairTouchModeMemberRank.getRankByAchieve(totalAchieve,directSalePairTouchMode.getMemberRanks());
            if (userMaxRank==null) continue;
            String sa=userRank==null?"无":userRank.getSalutation();
            logger.info(user.getPhone() + "现为:" + sa + ",最高可达 " + userMaxRank.getSalutation());
            if (userMaxRank.getCashBonus()==0d&&userMaxRank.getMaterialBonus()==null&&userMaxRank.getSalutation()==null) continue;
            int beginOrdinary=userRank==null?1:userRank.getOrdinary()+1;
            inner:for (int i=beginOrdinary;i<=userMaxRank.getOrdinary();i++){
                PairTouchModeMemberRank nextRank=PairTouchModeMemberRank.getRankByOrdinary(i,directSalePairTouchMode.getMemberRanks());
                if (nextRank==null) break inner;
                if (nextRank.getCashBonus()==0d&&nextRank.getMaterialBonus()==null&&nextRank.getSalutation()==null) continue inner;
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
//                userMeasure.setSort(UserMeasureSortEnum.XIAOQU.toCode());
                userMeasure.setRank(nextRank);
                userMeasure.setType(1);
                userMeasure.setUser(user);
                logger.info(user.getPhone() + ":" + userMeasure.getNote());
                littleAreaBonusMeasures.add(userMeasure);
            }
            user.setRank(userMaxRank);
            userService.update(user);
        }
        insertAll(littleAreaBonusMeasures);

    }

    private PairTouchModeMemberRank findUserRank(User user) {
        if (user==null) return null;
        List<UserMeasure> littleAreaUserMeasures=findLittleAreaUserMeasuresByUser(user);
        int maxOrdinary=0;
        for (UserMeasure userMeasure:littleAreaUserMeasures){
            PairTouchModeMemberRank rank=userMeasure.getRank();
            if (rank==null) continue;
            if(rank.getOrdinary()>maxOrdinary) maxOrdinary=rank.getOrdinary();
        }
        return PairTouchModeMemberRank.getRankByOrdinary(maxOrdinary,directSalePairTouchMode.getMemberRanks());
    }

    private List<UserMeasure> findLittleAreaUserMeasuresByUser(User user) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(user.getId())));
        dbObject.put("rank",new BasicDBObject("$exists",true));
        return findAll(dbObject);
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
        TestUser upper=new TestUser();
        upper.setLevel(3);
        upper.setOrderInLevel(2);
        TestUser lower=new TestUser();
        lower.setLevel(5);
        lower.setOrderInLevel(6);
        boolean little=isInSmallArea(lower,upper);
        System.out.println(little);
    }

//    private List<TestUser> allUsers;
    public int testMaxLevel(int level,int levelUserCount,int totalUserCount,Map<Integer, Integer> levelUserCountMap, Map<Integer, Integer> totalUserCountMap, Map<Integer, List<TestUser>> levelUsersMap){
        if(true)
            return testMaxLevel2(level,levelUserCount,totalUserCount,levelUserCountMap,totalUserCountMap,levelUsersMap,0);
        level++;
        System.out.println("---------------------------------------------------     level "+level+"   ---------------------------------------------------  ");
        levelUserCount*=2;
        totalUserCount+=levelUserCount;
        levelUserCountMap.put(level, levelUserCount);
        totalUserCountMap.put(level, totalUserCount);
        System.out.println("levelUserCount:"+levelUserCount+",totalUserCount:"+totalUserCount);
        double duipengBonus=directSalePairTouchMode.getPairTouchRate()*directSalePairTouchMode.getMembershipLine();
        double zhituiBonus=(directSalePairTouchMode.getDirectPushRateMarketBig()+directSalePairTouchMode.getDirectPushRateMarketLittle())/2*directSalePairTouchMode.getMembershipLine();
        double jiandianBonus=directSalePairTouchMode.getAnyPointBonus();
        double maxBonusPerDay = directSalePairTouchMode.getMaxBonusPerDay();


        List<TestUser> levelUsers=new ArrayList<TestUser>();
        //逐个增加新一层用户
        double levelIncome=0d;
        double levelSpend=0d;
        for (int i=1;i<=levelUserCount;i++){
            double personSpend=0d;
            double personIncome=0d;
            personIncome+=directSalePairTouchMode.getMembershipLine();
            TestUser levelUser=new TestUser();
            levelUser.setLevel(level);
            levelUser.setOrderInLevel(i);
//            System.out.println("===============  user order:"+i+"   ===============");
            levelUsers.add(levelUser);
            int order = i % 2 == 0 ? i / 2 : (i + 1)/2;
            TestUser upperOfLevelUser=getUserByLevelAndOrder(level-1, order,levelUsersMap);
            if ((i)%2==0){
                //此时对碰+直推+见点奖因为是一起拿到的，不可拆分，所以必须一起算
                if (duipengBonus+zhituiBonus+jiandianBonus>= maxBonusPerDay){//这里做最悲观估计，单笔对碰奖就达到每日最大奖励
                    upperOfLevelUser.setBonus(upperOfLevelUser.getBonus()+ maxBonusPerDay);
                    personSpend+= maxBonusPerDay;
                }else{
                    upperOfLevelUser.setBonus(upperOfLevelUser.getBonus()+duipengBonus+zhituiBonus+jiandianBonus);
                    personSpend+=duipengBonus+zhituiBonus+jiandianBonus;
                }
            }else{
                //直推+见点，没有对碰，同样不可拆分
                if (zhituiBonus+jiandianBonus>= maxBonusPerDay){//这里做最悲观估计
                    upperOfLevelUser.setBonus(upperOfLevelUser.getBonus()+ maxBonusPerDay);
                    personSpend+= maxBonusPerDay;
                }else{
                    upperOfLevelUser.setBonus(upperOfLevelUser.getBonus()+zhituiBonus+jiandianBonus);
                    personSpend+=zhituiBonus+jiandianBonus;
                }
            }
            levelUsersMap.put(level,levelUsers);
            //见点奖，前面已经算过了level-1层的见点奖，所以要从level-2层开始倒推
            if (level>2) {
                a:for (int theLevel=level-2;theLevel>=level-directSalePairTouchMode.getAnyPointFloors();theLevel--){
                    if (theLevel<=0) break a;
                    for (int j=1;j<=levelUserCountMap.get(theLevel);j++){
                        TestUser theUser=getUserByLevelAndOrder(theLevel, j,levelUsersMap);
                        if (jiandianBonus>= maxBonusPerDay){//这里做最悲观估计
                            theUser.setBonus(theUser.getBonus()+ maxBonusPerDay);
                            personSpend+= maxBonusPerDay;
                        }else{
                            theUser.setBonus(theUser.getBonus()+jiandianBonus);
                            personSpend+=jiandianBonus;
                        }
                    }

                }
            }
            levelIncome+=personIncome;
            levelSpend+=personSpend;
            if (directSalePairTouchMode.getMembershipLine()<personSpend){
                System.out.println("spend:"+BigDecimalUtil.format_twoDecimal(personSpend)+",level:"+level+",order:"+order);
                return level;

            }
        }
        System.out.println("level income:"+(BigDecimalUtil.format_twoDecimal(levelIncome))+",level spend:"+BigDecimalUtil.format_twoDecimal(levelSpend));


        if(level>50) {
            System.out.println("too many levels");
            return level;
        }
        return testMaxLevel(level,levelUserCount,totalUserCount,levelUserCountMap,totalUserCountMap,levelUsersMap);
    }
    public int testMaxLevel2(int level,int levelUserCount,int totalUserCount,Map<Integer, Integer> levelUserCountMap, Map<Integer, Integer> totalUserCountMap, Map<Integer, List<TestUser>> levelUsersMap,double totalIncome){

        level++;
        System.out.println("---------------------------------------------------     第 "+level+" 层  ---------------------------------------------------  ");
        levelUserCount*=2;
        totalUserCount+=levelUserCount;
        levelUserCountMap.put(level, levelUserCount);
        totalUserCountMap.put(level, totalUserCount);
        System.out.println("本层用户数:"+levelUserCount+",总用户数:"+totalUserCount);
        double duipengBonus=directSalePairTouchMode.getPairTouchRate()*directSalePairTouchMode.getMembershipLine();
        double zhituiBonus=(directSalePairTouchMode.getDirectPushRateMarketBig()+directSalePairTouchMode.getDirectPushRateMarketLittle())/2*directSalePairTouchMode.getMembershipLine();
        double jiandianBonus=directSalePairTouchMode.getAnyPointBonus();
        double maxBonusPerDay = directSalePairTouchMode.getMaxBonusPerDay();


        List<TestUser> levelUsers=new ArrayList<TestUser>();
        //逐个增加新一层用户
        double levelIncome=0d;
        double levelSpend=0d;
        int levelDuipengTimes=0;
        int levelZhituiTimes=0;
        for (int i=1;i<=levelUserCount;i++){
            double personSpend=0d;
            double personIncome=0d;
            personIncome+=directSalePairTouchMode.getMembershipLine();
            TestUser levelUser=new TestUser();
            levelUser.setLevel(level);
            levelUser.setOrderInLevel(i);

            levelUsers.add(levelUser);
            levelUsersMap.put(level,levelUsers);
            int order = i % 2 == 0 ? i / 2 : (i + 1)/2;
            TestUser upperOfLevelUser=getUserByLevelAndOrder(level-1, order,levelUsersMap);//level层第i个用户的上级用户

            //直推奖
            if (zhituiBonus>= maxBonusPerDay){//这里做最悲观估计
                upperOfLevelUser.setBonus(upperOfLevelUser.getBonus()+ maxBonusPerDay);
                personSpend+= maxBonusPerDay;

            }else{
                upperOfLevelUser.setBonus(upperOfLevelUser.getBonus()+zhituiBonus);
                personSpend+=zhituiBonus;
            }
            levelZhituiTimes++;
            //对碰奖
            a:for (int theLevel=level-1;theLevel>=1;theLevel--){
                b:for (int j=1;j<=levelUserCountMap.get(theLevel);j++){
                    //判断第i个用户是否在第theLevel层第j个用户的小区
                    boolean inSmallArea=isInSmallArea(levelUser,getUserByLevelAndOrder(theLevel, j,levelUsersMap));
                    if (!inSmallArea) continue b;
                    TestUser theUser=getUserByLevelAndOrder(theLevel, j,levelUsersMap);
                    if (duipengBonus>= maxBonusPerDay){//这里做最悲观估计
                        theUser.setBonus(theUser.getBonus()+ maxBonusPerDay);
                        personSpend+= maxBonusPerDay;
                        levelDuipengTimes++;
//                        System.out.println("第"+theLevel+"层第"+j+"个用户获得第"+level+"层第"+i+"个用户对碰奖"+maxBonusPerDay+"(已达最大奖励)");
                    }else{
                        theUser.setBonus(theUser.getBonus()+duipengBonus);
                        personSpend+=duipengBonus;
                        levelDuipengTimes++;
//                        System.out.println("第"+theLevel+"层第"+j+"个用户获得第"+level+"层第"+i+"个用户对碰奖"+duipengBonus);
                    }
                }
            }

            levelIncome+=personIncome;
            levelSpend+=personSpend;
//            if (directSalePairTouchMode.getMembershipLine()<personSpend){
//                System.out.println("spend:"+BigDecimalUtil.format_twoDecimal(personSpend)+",level:"+level+",order:"+order);
//                return level;
//
//            }
        }
        totalIncome+=levelIncome-levelSpend;
        double levelChengben=1700*levelUserCount;
        System.out.println("本层收入:"+(BigDecimalUtil.format_twoDecimal(levelIncome))+",成本："+BigDecimalUtil.format_twoDecimal(levelChengben)+",支出:"+BigDecimalUtil.format_twoDecimal(levelSpend)+",本层净收入"+BigDecimalUtil.format_twoDecimal(levelIncome-levelSpend-levelChengben)+"，全部收入："+totalIncome+",本层支出直推奖"+levelZhituiTimes+"次，对碰奖"+levelDuipengTimes+"次");

        if (levelIncome-levelSpend-levelChengben<0){
            System.out.println("该层收入为负");
            return level;
        }
        if(level>50) {
            System.out.println("too many levels");
            return level;
        }
        return testMaxLevel2(level, levelUserCount, totalUserCount, levelUserCountMap, totalUserCountMap, levelUsersMap,totalIncome);
    }

    private static boolean isInSmallArea(TestUser lowerUser, TestUser upperUser) {
        Assert.isTrue(lowerUser.getOrderInLevel()>0);
        Assert.isTrue(upperUser.getOrderInLevel()>0);
        Assert.isTrue(upperUser.getLevel()>0);
        Assert.isTrue(lowerUser.getLevel()>0);
        int lowerOrder=lowerUser.getOrderInLevel();
        int upperOrder=upperUser.getOrderInLevel();
        int levelCha=lowerUser.getLevel()-upperUser.getLevel()  ;//层差
        //上级用户的序号乘上2乘层数差次方即为下级用户所在层的小区序号最大值
        //序号最大值减去2的层差减一次方加一为小区序号最小值
        int maxLittleAreaOrder=new Double(upperOrder*Math.pow(2,levelCha )).intValue();
        int minLittleAreaOrder=maxLittleAreaOrder-new Double(Math.pow(2,levelCha-1)).intValue()+1;
        if (lowerOrder>=minLittleAreaOrder&&lowerOrder<=maxLittleAreaOrder) return true;
        return false;
    }

    private TestUser getUserByLevelAndOrder(int level, int order,Map<Integer,List<TestUser>> levelUsersMap) {
        if (levelUsersMap==null) return null;
        List<TestUser> users=levelUsersMap.get(level);
        if (users==null) return null;
        for (TestUser testUser:users){
            if (testUser.getOrderInLevel()==order) return testUser;
        }
        return null;
    }


    public List<UserMeasure> findIncomeByUser(User user) {
        if (user==null) return null;
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(user.getId())));
        dbObject.put("type",1);
        return findAll(dbObject);
    }

    public List<UserMeasure> findByUser(User user) {
        if (user==null) return null;
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(user.getId())));
        return findAll(dbObject);
    }
}
