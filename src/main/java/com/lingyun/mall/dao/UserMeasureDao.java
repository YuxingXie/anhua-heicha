package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.code.UserMeasureSortEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.DateUtil;
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
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        dateBetween.put("$gt", DateUtil.getYesterday235959());
        dateBetween.put("$lt", DateUtil.getTodayZeroHour());
        dbObject.put("becomeMemberDate", dateBetween);
         List<User> newMemberUsers=ServiceManager.userService.findAll(dbObject);
        int num=newMemberUsers==null?0:newMemberUsers.size();
        logger.info("昨日注册用户数："+num);
        if (num==0) return;
        settleAnyPointBonus(newMemberUsers);
        settleDirectPushBonus(newMemberUsers);
        settlePairTouchBonus(newMemberUsers);
        settleLittleAreaBonus();
    }
    //对碰奖
    private void settlePairTouchBonus(List<User> newMemberUsers) {
        logger.info("发放对碰奖");
        List<User> directUpperUsers= userService.getDirectUpperUsers(newMemberUsers);
        Date now=new Date();
        for (User upperUser :directUpperUsers){
            List<User> directLowerUsers=userService.findLowerOrUpperUsers(upperUser, 1);
            if (directLowerUsers==null||directLowerUsers.size()!=2) continue;
            UserMeasure userMeasure=new UserMeasure();
            double yesterdayBonus=getTotalBonusYesterday(upperUser);
            logger.info(upperUser.getPhone()+":昨日奖励:"+yesterdayBonus);
            if (yesterdayBonus>=directSalePairTouchMode.getMaxBonusPerDay()) {
                logger.info(upperUser.getPhone() + ":昨日奖励已达最大");
                continue;
            }
            if (yesterdayBonus+directSalePairTouchMode.getPairTouchRate()*directSalePairTouchMode.getMembershipLine()>=directSalePairTouchMode.getMaxBonusPerDay()){
                userMeasure.setCount(directSalePairTouchMode.getMaxBonusPerDay()-yesterdayBonus);
                userMeasure.setNote("您获得对碰奖奖励"+ BigDecimalUtil.format_twoDecimal(userMeasure.getCount())+"元,您的每日奖励已达封顶。");
            }else{
                userMeasure.setCount(directSalePairTouchMode.getPairTouchRate()*directSalePairTouchMode.getMembershipLine());
                userMeasure.setNote("您获得对碰奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
            }
            logger.info(upperUser.getPhone() + ":" + userMeasure.getNote());
            userMeasure.setDate(now);
            userMeasure.setSort(UserMeasureSortEnum.DUIPENG.toCode());
            userMeasure.setType(1);
            userMeasure.setUser(upperUser);
            insert(userMeasure);
        }

    }



    //直推奖
    private void settleDirectPushBonus(List<User> newMemberUsers) {
        logger.info("发放直推奖");
        Date now=new Date();
        for (User newMembershipUser :newMemberUsers){
            User directUpperUser=userService.getDirectUpperUser(newMembershipUser);
            if (directUpperUser==null) continue;
            UserMeasure userMeasure=new UserMeasure();
            double yesterdayBonus=getTotalBonusYesterday(directUpperUser);
            double bonus=directSalePairTouchMode.getDirectPushRate()*directSalePairTouchMode.getMembershipLine();
            if (yesterdayBonus>=directSalePairTouchMode.getMaxBonusPerDay()) continue;
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
            insert(userMeasure);
        }

//        insertAll(directPushMeasures);
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
                }else{
                    userMeasure.setCount(directSalePairTouchMode.getAnyPointBonus());
                    userMeasure.setNote("用户" + newMemberUser.getPhone() + "为您提供见点奖奖励" + BigDecimalUtil.format_twoDecimal(userMeasure.getCount()) + "元。");
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
        DBObject dbObject=new BasicDBObject();
        dbObject.put("directSaleMember",true);
        DBObject dateBetween=new BasicDBObject();
        dateBetween.put("$ge", DateUtil.getYesterday235959());
        dateBetween.put("$lt", DateUtil.getTodayZeroHour());
        dbObject.put("becomeMemberDate", dateBetween);

        System.out.println(dbObject);
    }

//    private List<TestUser> allUsers;
    public int testMaxLevel(int level,int levelUserCount,int totalUserCount,Map<Integer, Integer> levelUserCountMap, Map<Integer, Integer> totalUserCountMap, Map<Integer, List<TestUser>> levelUsersMap){
        level++;
        System.out.println("---------------------------------------------------     level "+level+"   ---------------------------------------------------  ");
        levelUserCount*=2;
        totalUserCount+=levelUserCount;
        levelUserCountMap.put(level, levelUserCount);
        totalUserCountMap.put(level, totalUserCount);
        System.out.println("levelUserCount:"+levelUserCount+",totalUserCount:"+totalUserCount);
        double duipengBonus=directSalePairTouchMode.getPairTouchRate()*directSalePairTouchMode.getMembershipLine();
        double zhituiBonus=directSalePairTouchMode.getDirectPushRate()*directSalePairTouchMode.getMembershipLine();
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

    private TestUser getUserByLevelAndOrder(int level, int order,Map<Integer,List<TestUser>> levelUsersMap) {
        if (levelUsersMap==null) return null;
        List<TestUser> users=levelUsersMap.get(level);
        if (users==null) return null;
        for (TestUser testUser:users){
            if (testUser.getOrderInLevel()==order) return testUser;
        }
        return null;
    }




}
